/*
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NavbarComponent } from '../../components/navbar/navbar';
import { FooterComponent } from '../../components/footer/footer';
import { RestaurantDishListComponent } from '../../components/restaurant-dish-list/restaurant-dish-list.component';
import { RestaurantScheduleSidebarComponent } from '../../components/restaurant-schedule-sidebar/restaurant-schedule-sidebar.component';
import { OrderCartComponent } from '../../components/order-card/order-cart.component';

import { Dish } from '../../models/dish.models';
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';

/**
 * Tu pourras déplacer cette interface dans src/app/models/restaurant.model.ts
 * si tu préfères, mais pour l'instant on la garde ici.
 */
/*
interface Restaurant {
  id: number | string;
  name: string;
  dishes: Dish[];
  restaurantSchedules: RestaurantSchedule[];
}

/**
 * Modèle local du panier (doit correspondre à ce qu'attend OrderCartComponent)
 */
/*interface CartItem {
  dish: Dish;
  quantity: number;
}

/**
 * Service RESTAURANT : on suppose que tu vas l'implémenter
 * avec un appel HTTP vers ton backend Java (API REST existante).
 * Ici on ne DEVINE PAS l’URL ; on se contente d'utiliser une méthode
 * getRestaurantById que tu implémenteras toi-même.
 */
/*import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  // ⚠️ À TOI d'implémenter les vrais appels HTTP vers ton API Java.
  // Par exemple :
  // constructor(private http: HttpClient) {}
  //
  // getRestaurantById(id: string): Observable<Restaurant> {
  //   return this.http.get<Restaurant>(`/api/restaurants/${id}`);
  // }

  // Version temporaire pour que le front compile (à remplacer)
  getRestaurantById(_id: string): Observable<Restaurant> {
    // retourne un observable vide, juste pour éviter l'erreur
    return of({
      id: _id,
      name: 'Restaurant',
      dishes: [],
      restaurantSchedules: []
    });
  }
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

  restaurant: Restaurant | null = null;
  isLoading = true;
  errorMessage: string | null = null;

  cartItems: CartItem[] = [];
  selectedSchedule: RestaurantSchedule | undefined;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService
  ) {}

  ngOnInit(): void {
    // On récupère l'id dans l'URL: /restaurant/:id/menu
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage = 'Aucun restaurant sélectionné.';
      this.isLoading = false;
      return;
    }

    this.loadRestaurant(id);
  }

  private loadRestaurant(id: string): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.restaurantService.getRestaurantById(id).subscribe({
      next: (restaurant) => {
        this.restaurant = restaurant;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Impossible de charger ce restaurant.';
        this.isLoading = false;
      }
    });
  }

  // --------- Gestion des créneaux ---------

  onScheduleSelected(schedule: RestaurantSchedule): void {
    this.selectedSchedule = schedule;
  }

  // --------- Gestion du panier ---------

  onAddDish(event: { dish: Dish; quantity: number }): void {
    const { dish, quantity } = event;

    const existing = this.cartItems.find(i => i.dish.name === dish.name);
    if (existing) {
      existing.quantity += quantity;
    } else {
      this.cartItems.push({ dish, quantity });
    }
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
    this.cartItems = this.cartItems.filter(i => i.dish.name !== dish.name);
  }

  onConfirmOrder(): void {
    // Ici tu pourras appeler ton OrderService / API Java pour créer l'Order
    // en utilisant restaurant, cartItems, selectedSchedule, etc.
    console.log('Commande confirmée :', {
      restaurant: this.restaurant,
      schedule: this.selectedSchedule,
      items: this.cartItems
    });
  }
}
*/


import { CommonModule } from '@angular/common';
import { Component, OnInit, Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NavbarComponent } from '../../components/navbar/navbar';
import { FooterComponent } from '../../components/footer/footer';
import { RestaurantDishListComponent } from '../../components/restaurant-dish-list/restaurant-dish-list.component';
import { RestaurantScheduleSidebarComponent } from '../../components/restaurant-schedule-sidebar/restaurant-schedule-sidebar.component';
import { OrderCartComponent } from '../../components/order-card/order-cart.component';

import { Dish } from '../../models/dish.models';
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';

import {Observable, of, tap} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {DishModel} from '../../models/DishModel';
import {DishCreateResponse} from '../../models/dishCreateResponse';
import {orderService} from '../../services/orderService';
import {orderStateModel} from '../../models/orderStateModel';

interface Restaurant {
  id: number | string;
  name: string;
  dishes: DishModel[];
  restaurantSchedules: RestaurantSchedule[];
}

interface CartItem {
  dish: Dish;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class RestaurantService {

  private readonly MOCK_RESTAURANT: Restaurant = {
    id: '1',
    name: 'SophiaEat - Restaurant de test',
    dishes: [
      new DishModel({
        name: 'Pizza Margherita',
        description: 'Tomate, mozzarella, basilic frais',
        price: 11.9,
        category: 'Main_Course',
        type: 'Pizza',
        dishTag: ['Végétarien'],
        availableToppings: [],
        selectedToppings: []
      }),
      new DishModel({
        name: 'Burger Classique',
        description: 'Bœuf, cheddar, salade, tomate, sauce maison',
        price: 13.5,
        category: 'Starter',
        type: 'Meat',
        dishTag: ['Best-seller'],
        availableToppings: [],
        selectedToppings: []
      }),
      new DishModel({
        name: 'Tiramisu',
        description: 'Dessert italien au café',
        price: 6.0,
        category: 'Dessert',
        type: 'Cake',
        dishTag: [],
        availableToppings: [],
        selectedToppings: []
      }),
    ],
    restaurantSchedules: [
      { id: 'slot1', day: 'MONDAY',    start: '11:30', end: '12:00', capacity: 10 },
      { id: 'slot2', day: 'MONDAY',    start: '12:00', end: '12:30', capacity: 15 },
      { id: 'slot3', day: 'TUESDAY',   start: '19:00', end: '19:30', capacity: 8  },
      { id: 'slot4', day: 'TUESDAY',   start: '19:30', end: '20:00', capacity: 8  },
      { id: 'slot5', day: 'WEDNESDAY', start: '12:00', end: '12:30', capacity: 12 }
    ]
  };

  constructor() {}

  getRestaurantById(_id: string): Observable<Restaurant> {
    console.log('[MOCK] RestaurantService.getRestaurantById(', _id, ')');
    return of(this.MOCK_RESTAURANT);
  }
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
  dishRepoUrl = 'http://localhost:8004/api/dishRepo/';
  restaurant: Restaurant | null = null;
  isLoading = true;
  errorMessage: string | null = null;
  currentOrderId : number | null = null;
  cartItems: CartItem[] = [];
  selectedSchedule: RestaurantSchedule | undefined;
  orderState: orderStateModel | null = null;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private httpClient : HttpClient,
    private orderService : orderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') || '1';
    this.loadRestaurant(id);
    this.orderService.orderState$.subscribe((state) => {
      this.orderState = state;
      this.rebuildCartFromOrderState();
    });
    this.orderService.currentOrderId$.subscribe((id)=>{
      this.currentOrderId=id;
    })
  }

  private loadRestaurant(id: string): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.restaurantService.getRestaurantById(id).subscribe({
      next: (restaurant) => {
        this.restaurant = restaurant;
        restaurant.dishes.forEach((dish) => {
          this.httpClient.post<DishCreateResponse>(this.dishRepoUrl, dish.toCreateJson())
            .subscribe({
              next: (response) => {
                console.log('Plat bien ajouté :', dish.name, response);
                dish.setId(response.id);
                console.log('Nouvel id :',dish.id);
              },
              error: (err) => {
                console.error('Erreur de création pour', dish.name, err);
              }
            });
        });
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Impossible de charger ce restaurant.';
        this.isLoading = false;
      }
    });
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
      quantity : event.quantity
    };

    this.orderService.addItem(dishId,body).subscribe({
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
      console.error('Dish sans id, impossible de l’ajouter à la commande');
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
    if (!this.restaurant || !this.selectedSchedule || this.cartItems.length === 0) {
      console.warn('Commande incomplète');
      return;
    }

    console.log('Total envoyé à la page paiement :', this.orderState!.price);

    this.router.navigate(['/payment', this.currentOrderId], {
      state: {
        totalAmount: this.orderState!.price
      }
    });
  }

}
