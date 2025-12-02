import {Component, EventEmitter, Input, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {RestaurantService} from '../../services/restaurantService';
import {AccountService} from '../../services/accountService';
import {switchMap} from 'rxjs';
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
  constructor(private router: Router,private restaurantService : RestaurantService,private accountService:AccountService,private authService: AuthService) {
    this.authService.isLoggedIn$.subscribe((status: boolean) => {
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
  goToDetails(): void {
    if (!this.restaurant?.name) return;
    this.router.navigate(['/restaurant-detail', this.restaurant.name]);
  }
  createOrder() {
    this.authService.lastUserId$
      .pipe(
        switchMap(id =>
          this.restaurantService.createOrder({
            clientId: id,
            restaurantId: this.restaurant.name
          })
        )
      )
      .subscribe({
        next: res => console.log("Commande créée avec l'id",res),
        error: err => console.error("Erreur API :", err)
      });
  }
}
