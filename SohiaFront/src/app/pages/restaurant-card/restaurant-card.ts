import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-restaurant-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-card.html',
  styleUrl: './restaurant-card.css'
})
export class RestaurantCardComponent {
  @Input() restaurant: any;

  constructor(private router: Router) {}

  goToDetails() {
    // plus tard on redirigera vers la page de détail du restaurant
    console.log('👉 Restaurant cliqué :', this.restaurant.name);
  }
}
