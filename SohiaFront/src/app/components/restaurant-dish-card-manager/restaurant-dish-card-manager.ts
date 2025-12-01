import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Dish,
  DishCategory, DishCategoryLabel,
  DishTag, DishTagLabel,
  DishType, DishTypeLabel
} from "../../models/dish.models";



@Component({
  selector: 'se-restaurant-dish-card-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './restaurant-dish-card-manager.html',
  styleUrls: ['./restaurant-dish-card-manager.css']
})

export class RestaurantDishCardManagerComponent {
  @Input() dish!: Dish;
  @Output() update = new EventEmitter<{ oldId: number, newDish: Dish }>();
  @Output() delete = new EventEmitter<string>();

  isEditing = false;
  tempDish!: Dish;
  categories = Object.values(DishCategory);
  types = Object.values(DishType);
  allTags = Object.values(DishTag);

  catLabels = DishCategoryLabel;
  typeLabels = DishTypeLabel;
  tagLabels = DishTagLabel;

  startEdit() {
    this.tempDish = JSON.parse(JSON.stringify(this.dish));
    this.isEditing = true;
  }

  cancelEdit() {
    this.isEditing = false;
  }

  toggleTempTag(tag: DishTag) {
    const index = this.tempDish.dishTag.indexOf(tag);
    if (index > -1) {
      this.tempDish.dishTag.splice(index, 1);
    } else {
      this.tempDish.dishTag.push(tag);
    }
  }

  onSave() {
    console.log('Enfant - Sauvegarde :', { original: this.dish.id, modified: this.tempDish });
    if (!this.dish || !this.dish.id) {
      console.error("Erreur : Le plat original n'a pas d'id !", this.dish);
      return;
    }
    this.update.emit({
      oldId: this.dish.id!,
      newDish: this.tempDish
    });
    this.isEditing = false;
  }

  onDelete() {
    if(confirm(`Supprimer ${this.dish.name} ?`)) {
      this.delete.emit(this.dish.name);
    }
  }
}
