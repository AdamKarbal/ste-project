import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurantService';
import { Restaurant } from '../../models/restaurant.model';
import { CuisineType } from '../../models/cuisine-type.model';
import { EstablishmentType } from '../../models/establishment-type.model';
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';
import { RestaurantScheduleService } from '../../services/restaurant-schedule.service';
import { ActivatedRoute } from '@angular/router';
import { RestaurantDishCardManagerComponent } from '../../components/restaurant-dish-card-manager/restaurant-dish-card-manager';
import { RestaurantSettingsComponent } from '../../components/restaurant-settings.component/restaurant-settings.component';
import { RestaurantScheduleSidebarManagerComponent } from '../../components/restaurant-schedule-sidebar-manager/restaurant-schedule-sidebar-manager.component';

import {
  Dish,
  DishCategory, DishCategoryLabel,
  DishTag, DishTagLabel,
  DishType, DishTypeLabel
} from '../../models/dish.models';
import {NavbarManagingComponent} from '../../components/navbarmanaging/navbarmanaging';

@Component({
  selector: 'app-restaurant-manager-page',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    RestaurantDishCardManagerComponent,
    RestaurantSettingsComponent,
    RestaurantScheduleSidebarManagerComponent, NavbarManagingComponent
  ],
  templateUrl: './managing.html',
  styleUrls: ['./managing.css']
})

export class RestaurantManagerPageComponent implements OnInit {
  restaurantName = '';
  restaurant: Restaurant | null = null;

  // ---------- PLATS ----------
  isCreatingDish = false;
  newDish: Dish = this.getEmptyDish();
  categories = Object.values(DishCategory);
  types = Object.values(DishType);
  allTags = Object.values(DishTag);
  catLabels = DishCategoryLabel;
  typeLabels = DishTypeLabel;
  tagLabels = DishTagLabel;

  // ---------- CRÉNEAUX ----------
  schedules: RestaurantSchedule[] = [];
  selectedScheduleId?: string;
  isCreatingSchedule = false;
  editingSchedule: RestaurantSchedule | null = null;

  // petite popin de suppression
  scheduleToDelete: RestaurantSchedule | null = null;
  isDeleteDialogOpen = false;

  daysOfWeek = [
    { value: 'MONDAY', label: 'Lundi' },
    { value: 'TUESDAY', label: 'Mardi' },
    { value: 'WEDNESDAY', label: 'Mercredi' },
    { value: 'THURSDAY', label: 'Jeudi' },
    { value: 'FRIDAY', label: 'Vendredi' },
    { value: 'SATURDAY', label: 'Samedi' },
    { value: 'SUNDAY', label: 'Dimanche' },
  ];

  scheduleForm: {
    id?: string;
    day: string;
    start: string;
    end: string;
    capacity: number;
  } = {
    day: 'MONDAY',
    start: '',
    end: '',
    capacity: 10
  };

  constructor(
    private service: RestaurantService,
    private route: ActivatedRoute,
    private scheduleService: RestaurantScheduleService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const nameFromUrl = params.get('name');
      if (nameFromUrl) {
        this.restaurantName = nameFromUrl;
        this.loadData();
        this.loadSchedules();
      } else {
        console.error("Aucun nom de restaurant fourni dans l'URL !");
      }
    });
  }

  /* ---------- PLATS ---------- */

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
    this.service.addDish(this.restaurantName, this.newDish).subscribe((dish) => {
      console.log("dish reçu",dish);
      this.loadData();
      this.isCreatingDish = false;
      this.newDish = this.getEmptyDish();
    });
  }

  onUpdateDish(event: { oldId: number, newDish: Dish }) {
    if (!event || !event.oldId || !event.newDish) {
      console.error("Erreur : Données de mise à jour incomplètes", event);
      return;
    }

    this.service.updateDish(
      this.restaurantName,
      event.oldId,
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

  /* ---------- CRÉNEAUX ---------- */

  loadSchedules(): void {
    if (!this.restaurantName) return;

    this.scheduleService.getSchedules(this.restaurantName).subscribe({
      next: (data) => {
        console.log('DEBUG schedules from back:', data);
        this.schedules = data;
      },
      error: (err) => console.error('Erreur getSchedules :', err)
    });
  }

  onScheduleSelected(s: RestaurantSchedule): void {
    this.selectedScheduleId = s.id;
  }

  // clic sur "Modifier" dans la sidebar → on ouvre le form en mode EDIT
  onEditSchedule(s: RestaurantSchedule) {
    this.isCreatingSchedule = true;
    this.editingSchedule = s;

    this.scheduleForm = {
      id: s.id,
      day: (s.day || '').toUpperCase(),
      start: this.toTimeString(s.start),
      end: this.toTimeString(s.end),
      capacity: s.capacity
    };
  }

  // clic sur "Supprimer" → on ouvre une petite popin
  onDeleteSchedule(s: RestaurantSchedule): void {
    this.scheduleToDelete = s;
    this.isDeleteDialogOpen = true;
  }

  // bouton "Annuler" de la popin
  cancelDeleteSchedule(): void {
    this.isDeleteDialogOpen = false;
    this.scheduleToDelete = null;
  }

  // bouton "Supprimer" de la popin
  confirmDeleteSchedule(): void {
    if (!this.scheduleToDelete) return;

    const idToDelete = this.scheduleToDelete.id; // on capture l'id AVANT

    this.scheduleService.deleteSchedule(this.restaurantName, idToDelete).subscribe({
      next: () => {
        this.loadSchedules();
        this.isDeleteDialogOpen = false;
        this.scheduleToDelete = null;

        // on utilise l'id capturé, pas this.scheduleToDelete
        if (this.selectedScheduleId === idToDelete) {
          this.selectedScheduleId = undefined;
        }
      },
      error: (err) => console.error('Erreur suppression créneau :', err)
    });
  }

  // sauvegarde : création OU édition
  saveSchedule(): void {
    const { id, day, start, end, capacity } = this.scheduleForm;

    // 🟢 MODE EDITION : on ne modifie QUE la capacité
    if (this.editingSchedule && id) {
      this.scheduleService.updateScheduleCapacity(
        this.restaurantName,
        id,
        Number(capacity)
      ).subscribe({
        next: () => {
          this.afterScheduleChange();
        },
        error: (err) => {
          console.error('Erreur update capacité créneau :', err);
        }
      });
      return;
    }

    // 🆕 MODE CREATION : on doit avoir jour + heures
    if (!day || !start || !end) {
      return;
    }

    this.scheduleService.addSchedule(this.restaurantName, {
      day: day.toUpperCase(),
      start,
      end,
      capacity: Number(capacity)
    }).subscribe({
      next: () => {
        this.afterScheduleChange();
      },
      error: (err) => {
        console.error('Erreur ajout créneau :', err);
      }
    });
  }

  private afterScheduleChange(): void {
    this.loadData();
    this.loadSchedules();

    this.isCreatingSchedule = false;
    this.editingSchedule = null;
    this.selectedScheduleId = undefined;

    this.scheduleForm = {
      day: 'MONDAY',
      start: '',
      end: '',
      capacity: 10
    };
  }

  /* === création de créneau comme pour les plats === */

  startScheduleCreation(): void {
    this.isCreatingSchedule = true;
    this.editingSchedule = null;
    this.scheduleForm = {
      day: 'MONDAY',
      start: '',
      end: '',
      capacity: 10
    };
  }

  cancelScheduleCreation(): void {
    this.isCreatingSchedule = false;
    this.editingSchedule = null;
  }

  // utile pour formatter dans le HTML (et dans la popin)
  toTimeString(
    time: string | { hour: number; minute: number; second?: number; nano?: number } | undefined
  ): string {
    if (!time) return '';

    if (typeof time === 'string') {
      return time.substring(0, 5);
    }

    const h = String(time.hour).padStart(2, '0');
    const m = String(time.minute ?? 0).padStart(2, '0');
    return `${h}:${m}`;
  }

  getDayLabel(day: string | undefined): string {
    if (!day) return '';
    switch (day.toUpperCase()) {
      case 'MONDAY': return 'Lundi';
      case 'TUESDAY': return 'Mardi';
      case 'WEDNESDAY': return 'Mercredi';
      case 'THURSDAY': return 'Jeudi';
      case 'FRIDAY': return 'Vendredi';
      case 'SATURDAY': return 'Samedi';
      case 'SUNDAY': return 'Dimanche';
      default: return day;
    }
  }
}
