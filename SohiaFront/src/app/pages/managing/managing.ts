import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurantService';
import { Restaurant } from '../../models/restaurant.model';
import { CuisineType } from "../../models/cuisine-type.model";
import { EstablishmentType } from "../../models/establishment-type.model";
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';
import { RestaurantDishCardManagerComponent } from '../../components/restaurant-dish-card-manager/restaurant-dish-card-manager';
import { RestaurantSettingsComponent } from '../../components/restaurant-settings.component/restaurant-settings.component';
import { RestaurantScheduleSidebarComponent } from '../../components/restaurant-schedule-sidebar/restaurant-schedule-sidebar.component';
import { ActivatedRoute } from '@angular/router';

import {
  Dish,
  DishCategory, DishCategoryLabel,
  DishTag, DishTagLabel,
  DishType, DishTypeLabel
} from "../../models/dish.models";

@Component({
  selector: 'app-restaurant-manager-page',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    RestaurantDishCardManagerComponent,
    RestaurantSettingsComponent,
    RestaurantScheduleSidebarComponent
  ],
  templateUrl: './managing.html',
  styleUrls: ['./managing.css']
})

export class RestaurantManagerPageComponent implements OnInit {
  restaurantName = '';
  restaurant: Restaurant | null = null;
  isCreatingDish = false;
  newDish: Dish = this.getEmptyDish();
  categories = Object.values(DishCategory);
  types = Object.values(DishType);
  allTags = Object.values(DishTag);
  catLabels = DishCategoryLabel;
  typeLabels = DishTypeLabel;
  tagLabels = DishTagLabel;

  constructor(private service: RestaurantService,private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const nameFromUrl = params.get('name');
      if (nameFromUrl) {
        this.restaurantName = nameFromUrl;
        this.loadData();
      } else {
        console.error("Aucun nom de restaurant fourni dans l'URL !");
      }
    });
  }

  getEmptyDish(): Dish {
    return {
      name: '',
      price: 0,
      description: '',
      category: DishCategory.MAIN_COURSE,
      type: DishType.OTHER,
      dishTag: []
    };
  }

  loadData() {
    this.service.getRestaurant(this.restaurantName).subscribe({
      next: (data) => this.restaurant = data,
      error: (err) => console.error(err)
    });
  }


  startCreation() { this.isCreatingDish = true; }

  cancelCreation() {
    this.isCreatingDish = false;
    this.newDish = this.getEmptyDish();
  }

  toggleTag(tag: DishTag) {
    const index = this.newDish.dishTag.indexOf(tag);
    if (index > -1) {
      this.newDish.dishTag.splice(index, 1);
    } else {
      this.newDish.dishTag.push(tag);
    }
  }

  createDish() {
    if (!this.newDish.name) return;
    this.service.addDish(this.restaurantName, this.newDish).subscribe(() => {
      this.loadData();
      this.isCreatingDish = false;
      this.newDish = this.getEmptyDish();
    });
  }

  onUpdateDish(event: { oldName: string, newDish: Dish }) {
    if (!event || !event.oldName || !event.newDish) {
      console.error("Erreur : Données de mise à jour incomplètes", event);
      return;
    }

    this.service.updateDish(
      this.restaurantName,
      event.oldName,
      event.newDish
    ).subscribe({
      next: () => this.loadData(),
      error: (err) => console.error('Erreur update plat:', err)
    });
  }

  onDeleteDish(name: string) {
    this.service.deleteDish(this.restaurantName, name).subscribe(() => this.loadData());
  }

  onUpdateEstablishment(type: EstablishmentType) {
    this.service.setEstablishmentType(this.restaurantName, type).subscribe(() => this.loadData());
  }
  onAddCuisine(type: CuisineType) {
    this.service.addCuisineType(this.restaurantName, type).subscribe(() => this.loadData());
  }
  onRemoveCuisine(type: CuisineType) {
    this.service.removeCuisineType(this.restaurantName, type).subscribe(() => this.loadData());
  }
}
