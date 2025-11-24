import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Dish } from '../../models/dish.models';
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';

export interface CartItem {
  dish: Dish;
  quantity: number;
}

@Component({
  selector: 'se-order-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-cart.component.html',
  styleUrls: ['./order-cart.component.css']
})
export class OrderCartComponent {

  /** Contenu du panier */
  @Input() items: CartItem[] = [];

  /** Créneau choisi pour la livraison */
  @Input() selectedSchedule?: RestaurantSchedule;

  /** Événements vers le parent */
  @Output() quantityChanged = new EventEmitter<{ dish: Dish; quantity: number }>();
  @Output() dishRemoved = new EventEmitter<Dish>();
  @Output() confirmOrder = new EventEmitter<void>();

  /** Total calculé à partir des items */
  get totalPrice(): number {
    return (this.items ?? []).reduce((sum, item) =>
      sum + (item.dish.price || 0) * (item.quantity || 0), 0
    );
  }

  /** Formatage du prix */
  formatPrice(value: number): string {
    return `${value.toFixed(2)} €`;
  }

  /** Affichage du créneau sélectionné */
  get selectedScheduleLabel(): string | null {
    if (!this.selectedSchedule) return null;
    const day = this.getDayLabel(this.selectedSchedule.day);
    const start = this.formatTime(this.selectedSchedule.start);
    const end = this.formatTime(this.selectedSchedule.end);
    return `${day} - ${start} → ${end}`;
  }

  private getDayLabel(day: string): string {
    switch (day) {
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

  private formatTime(time: string | undefined): string {
    if (!time) return '';
    return time.substring(0, 5); // "HH:mm"
  }

  // --------- actions utilisateur ----------

  onIncrease(item: CartItem): void {
    const newQuantity = item.quantity + 1;
    this.quantityChanged.emit({ dish: item.dish, quantity: newQuantity });
  }

  onDecrease(item: CartItem): void {
    const newQuantity = item.quantity - 1;
    if (newQuantity <= 0) {
      this.onRemove(item);
    } else {
      this.quantityChanged.emit({ dish: item.dish, quantity: newQuantity });
    }
  }

  onRemove(item: CartItem): void {
    this.dishRemoved.emit(item.dish);
  }

  onConfirmClick(): void {
    if (!this.items.length) return;
    this.confirmOrder.emit();
  }

  trackByDishName(_index: number, item: CartItem): string {
    return item.dish.name;
  }
}
