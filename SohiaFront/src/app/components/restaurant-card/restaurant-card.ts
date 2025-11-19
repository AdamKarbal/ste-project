import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-restaurant-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-card.html',
  styleUrl: './restaurant-card.css',
})
export class RestaurantCardComponent {
  @Input() restaurant: any;

  constructor(private router: Router) {}

  goToMenu() {
    if (!this.restaurant?.name) {
      console.warn('Restaurant sans nom, impossible de naviguer.');
      return;
    }

    console.log('Navigation vers le menu du restaurant :', this.restaurant.name);

    // ⚠️ Ne pas encoder manuellement, Angular le fait déjà
    this.router.navigate(['/restaurant-menu', this.restaurant.name]);
  }

}
