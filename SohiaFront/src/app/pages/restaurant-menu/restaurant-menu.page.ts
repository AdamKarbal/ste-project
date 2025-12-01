import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {NavbarComponent} from '../../components/navbar/navbar';
import {FooterComponent} from '../../components/footer/footer';
import {RestaurantDishListComponent} from '../../components/restaurant-dish-list/restaurant-dish-list.component';
import {
  RestaurantScheduleSidebarComponent
} from '../../components/restaurant-schedule-sidebar/restaurant-schedule-sidebar.component';
import {OrderCartComponent} from '../../components/order-card/order-cart.component';

import {Dish, DishCategory, DishTag, DishType} from '../../models/dish.models';
import {RestaurantSchedule} from '../../models/restaurant-schedule.model';

import {Observable, of, tap} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {DishModel} from '../../models/DishModel';
import {DishCreateResponse} from '../../models/dishCreateResponse';
import {orderService} from '../../services/orderService';
import {orderStateModel} from '../../models/orderStateModel';
import {RestaurantService} from '../../services/restaurantService';
import {RestaurantCarte} from '../../models/restaurant';

interface CartItem {
  dish: Dish;
  quantity: number;
}

@Component({
  selector: 'se-restaurant-menu-page',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    FooterComponent,
    RestaurantDishListComponent,
    RestaurantScheduleSidebarComponent,
    OrderCartComponent
  ],
  templateUrl: './restaurant-menu.page.html',
  styleUrls: ['./restaurant-menu.page.css']
})
export class RestaurantMenuPage implements OnInit {

  restaurant: RestaurantCarte | null = null;
  isLoading = true;
  errorMessage: string | null = null;

  currentOrderId: number | null = null;
  cartItems: CartItem[] = [];
  selectedSchedule: RestaurantSchedule | undefined;
  orderState: orderStateModel | null = null;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private httpClient: HttpClient,
    private orderService: orderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('name') || '1';

    this.loadRestaurant(id);
    this.orderService.orderState$.subscribe((state) => {
      this.orderState = state;
      this.rebuildCartFromOrderState();
    });

    this.orderService.currentOrderId$.subscribe((id) => {
      this.currentOrderId = id;
    });
  }

  private loadRestaurant(name: string): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.restaurantService.getRestaurantById(name).subscribe({
      next: (restaurant) => {
        console.log('🔍 Restaurant brut reçu depuis l’API :', restaurant);
        this.restaurant = restaurant;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Impossible de charger ce restaurant.';
        this.isLoading = false;
      }
    });
    this.restaurantService.getSchedulesToday(name).subscribe({
      next: (schedules)=>{
        console.log('schedules reçus',schedules);
        this.restaurant!.restaurantSchedules = schedules;
      },
      error:(err)=>{
        console.error(err);
      }
    })
  }

  private rebuildCartFromOrderState(): void {
    if (!this.orderState || !this.restaurant) {
      this.cartItems = [];
      return;
    }

    const dishesMap = this.orderState.dishes; // { [dishId]: quantity }
    const items: CartItem[] = [];

    Object.entries(dishesMap).forEach(([dishIdStr, quantity]) => {
      const dishId = Number(dishIdStr);
      const dish = this.restaurant!.dishes.find(d => (d as any).id === dishId);
      if (!dish) {
        console.warn('Dish id', dishId, 'introuvable dans restaurant.dishes');
        return;
      }
      items.push({ dish, quantity });
    });

    this.cartItems = items;
  }

  // --------- Gestion des créneaux ---------

  onScheduleSelected(schedule: RestaurantSchedule): void {
    this.selectedSchedule = schedule;
    this.orderService.chooseSchedule(schedule.id).subscribe({
      next: (schedule) => console.log("Nouvel etat de la commande",schedule),
      error: (err) => console.error(err)
    });
  }

  // --------- Gestion du panier ---------

  onAddDish(event: { dish: Dish; quantity: number }): void {
    const { dish } = event;
    const dishId = (dish as any).id;

    if (!dishId) {
      console.error('Dish sans id, impossible de l’ajouter à la commande');
      return;
    }

    const body = {
      quantity: event.quantity
    };

    this.orderService.addItem(dishId, body).subscribe({
      next: (state) => {
        console.log('Commande mise à jour :', state);
      },
      error: (err) => console.error('Erreur addItem :', err),
    });
  }

  onQuantityChanged(event: { dish: Dish; quantity: number }): void {
    const item = this.cartItems.find(i => i.dish.name === event.dish.name);
    if (!item) return;

    if (event.quantity <= 0) {
      this.cartItems = this.cartItems.filter(i => i !== item);
    } else {
      item.quantity = event.quantity;
    }
  }

  onDishRemoved(dish: Dish): void {
    const dishId = (dish as any).id;

    if (!dishId) {
      console.error('Dish sans id, impossible de l’enlever de la commande');
      return;
    }

    this.orderService.removeItem(dishId).subscribe({
      next: (state) => {
        console.log('Commande mise à jour (enlevé)', state);
      },
      error: (err) => console.error('Erreur removeItem :', err),
    });
  }

  onConfirmOrder(): void {
    if (!this.restaurant || !this.selectedSchedule || this.cartItems.length === 0 || !this.orderState) {
      console.warn('Commande incomplète');
      return;
    }

    console.log('Total envoyé à la page paiement :', this.orderState.price);

    this.router.navigate(['/payment', this.currentOrderId], {
      state: {
        totalAmount: this.orderState.price
      }
    });
  }
}
