import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Dish } from '../../models/dish.models';
import { RestaurantDishCardComponent } from '../restaurant-dish-card/restaurant-dish-card.component';

@Component({
  selector: 'se-restaurant-dish-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RestaurantDishCardComponent
  ],
  templateUrl: './restaurant-dish-list.component.html',
  styleUrls: ['./restaurant-dish-list.component.css']
})
export class RestaurantDishListComponent {

  @Input() dishes: Dish[] = [];

  @Output() addDish = new EventEmitter<{ dish: Dish; quantity: number }>();

  searchTerm = '';
  selectedCategory: string | 'all' = 'all';

  get filteredDishes(): Dish[] {
    let result = this.dishes ?? [];

    if (this.selectedCategory !== 'all') {
      result = result.filter(d => d.category === this.selectedCategory);
    }

    if (this.searchTerm.trim().length > 0) {
      const lower = this.searchTerm.toLowerCase();
      result = result.filter(d =>
        d.name.toLowerCase().includes(lower) ||
        (d.description ?? '').toLowerCase().includes(lower)
      );
    }

    return result;
  }

  get categories(): string[] {
    const set = new Set<string>();
    (this.dishes ?? []).forEach(d => {
      if (d.category) set.add(d.category);
    });
    return [...set];
  }

  onDishAdded(dish: Dish, quantity: number): void {
    this.addDish.emit({ dish, quantity });
  }

  onSelectCategory(category: string | 'all'): void {
    this.selectedCategory = category;
  }
}
