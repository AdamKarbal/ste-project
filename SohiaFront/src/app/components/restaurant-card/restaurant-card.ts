import {Component, EventEmitter, Input, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {RestaurantService} from '../../services/restaurantService';
import { AuthService } from '../../services/authService';

@Component({
  selector: 'app-restaurant-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-card.html',
  styleUrl: './restaurant-card.css',
})
export class RestaurantCardComponent {
  @Input() restaurant: any;
  isLoggedIn = false;

  constructor(private router: Router, private auth: AuthService,private restaurantService : RestaurantService) {
    this.auth.isLoggedIn$.subscribe((status: boolean) => {
      this.isLoggedIn = status;
    });
  }
  goToMenu() {
    if (!this.restaurant?.name) {
      console.warn('Restaurant sans nom, impossible de naviguer.');
      return;
    }

    console.log('Navigation vers le menu du restaurant :', this.restaurant.name);
    this.createOrder();
    this.router.navigate(['/restaurant-menu', this.restaurant.name]);
  }
  createOrder() {
    const orderBody = {
      clientId: 42,
      restaurantId: this.restaurant.name
    };

    this.restaurantService
      .createOrder(orderBody)
      .subscribe({
        next: res => console.log("Commande créée :", res),
        error: err => console.error("Erreur API :", err)
      });
  }

  goToDetails(): void {
    if (!this.restaurant?.name) return;
    this.router.navigate(['/restaurant-detail', this.restaurant.name]);
  }
}
