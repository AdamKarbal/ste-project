import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
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

  constructor(private router: Router, private auth: AuthService) {
    this.auth.isLoggedIn$.subscribe((status: boolean) => {
      this.isLoggedIn = status;
    });
  }

  goToMenu(): void {
    if (!this.restaurant?.name) return;
    this.router.navigate(['/restaurant-menu', this.restaurant.name]);
  }

  goToDetails(): void {
    if (!this.restaurant?.name) return;
    this.router.navigate(['/restaurant-detail', this.restaurant.name]);
  }
}
