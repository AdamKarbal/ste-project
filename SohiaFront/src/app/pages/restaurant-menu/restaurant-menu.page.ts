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
import { ActivatedRoute } from '@angular/router';

import { NavbarComponent } from '../../components/navbar/navbar';
import { FooterComponent } from '../../components/footer/footer';
import { RestaurantDishListComponent } from '../../components/restaurant-dish-list/restaurant-dish-list.component';
import { RestaurantScheduleSidebarComponent } from '../../components/restaurant-schedule-sidebar/restaurant-schedule-sidebar.component';
import { OrderCartComponent } from '../../components/order-card/order-cart.component';

import { Dish } from '../../models/dish.models';
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';

import { Observable, of } from 'rxjs';

/**
 * Tu pourras déplacer cette interface dans src/app/models/restaurant.model.ts
 * si tu préfères, mais pour l'instant on la garde ici.
 */
interface Restaurant {
  id: number | string;
  name: string;
  dishes: Dish[];
  restaurantSchedules: RestaurantSchedule[];
}

/**
 * Modèle local du panier (doit correspondre à ce qu'attend OrderCartComponent)
 */
interface CartItem {
  dish: Dish;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class RestaurantService {

  // 🔹 Restaurant MOCK pour tester le front sans backend
  private readonly MOCK_RESTAURANT: Restaurant = {
    id: '1',
    name: 'SophiaEat - Restaurant de test',
    dishes: [
      {
        name: 'Pizza Margherita',
        description: 'Tomate, mozzarella, basilic frais',
        price: 11.9,
        category: 'Pizza',
        type: 'MAIN',
        dishTag: ['Végétarien']
      },
      {
        name: 'Burger Classique',
        description: 'Bœuf, cheddar, salade, tomate, sauce maison',
        price: 13.5,
        category: 'Burger',
        type: 'MAIN',
        dishTag: ['Best-seller']
      },
      {
        name: 'Tiramisu',
        description: 'Dessert italien au café',
        price: 6.0,
        category: 'Dessert',
        type: 'DESSERT',
        dishTag: []
      }
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

  // Pour l’instant on ignore l’id et on renvoie toujours le mock
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
    // Si pas d'id -> on prend '1' par défaut pour tester
    const id = this.route.snapshot.paramMap.get('id') || '1';
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
    console.log('Commande confirmée :', {
      restaurant: this.restaurant,
      schedule: this.selectedSchedule,
      items: this.cartItems
    });
  }
}
