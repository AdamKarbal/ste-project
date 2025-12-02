import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Dish } from '../../models/dish.models';
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';
import {CartItem} from '../../models/cartItem';

@Component({
  selector: 'se-order-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-cart.component.html',
  styleUrls: ['./order-cart.component.css']
})
export class OrderCartComponent {

  @Input() orderTotal?: number;
  @Input() items: CartItem[] = [];
  @Input() selectedSchedule?: RestaurantSchedule;

  @Output() quantityChanged = new EventEmitter<{ dish: Dish; quantity: number }>();
  @Output() dishRemoved = new EventEmitter<Dish>();
  @Output() confirmOrder = new EventEmitter<void>();

  get totalPrice(): number {
    return this.orderTotal ?? 0;
  }

  formatPrice(value: number): string {
    return `${value.toFixed(2)} €`;
  }

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

  private formatTime(
    time: string | { hour: number; minute: number; second?: number; nano?: number } | undefined
  ): string {
    if (!time) return '';

    if (typeof time === 'string') {
      return time.substring(0, 5); // "HH:mm"
    }

    const h = String(time.hour).padStart(2, '0');
    const m = String(time.minute ?? 0).padStart(2, '0');
    return `${h}:${m}`;
  }


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
