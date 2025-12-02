import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dish } from '../../models/dish.models';


@Component({
  selector: 'se-restaurant-dish-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-dish-card.component.html',
  styleUrls: ['./restaurant-dish-card.component.css']
})
export class RestaurantDishCardComponent {

  @Input() dish!: Dish;
  quantity = 1;

  @Output() add = new EventEmitter<number>();

  onIncrease(): void {
    this.quantity++;
  }

  onDecrease(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  onAddToCart(): void {
    if (this.quantity <= 0) return;
    this.add.emit(this.quantity);
  }

  get formattedPrice(): string {
    return this.dish ? this.dish.price.toFixed(2) + ' €' : '';
  }
}
