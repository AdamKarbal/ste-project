import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';

@Component({
  selector: 'se-restaurant-schedule-sidebar-manager',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-schedule-sidebar-manager.component.html',
  styleUrls: ['./restaurant-schedule-sidebar-manager.component.css']
})
export class RestaurantScheduleSidebarManagerComponent {

  @Input() restaurantName: string | undefined;
  @Input() schedules: RestaurantSchedule[] = [];

  @Input() selectedScheduleId: string | undefined;

  @Output() scheduleSelected = new EventEmitter<RestaurantSchedule>();
  @Output() editSchedule = new EventEmitter<RestaurantSchedule>();
  @Output() deleteSchedule = new EventEmitter<RestaurantSchedule>();

  private readonly DAY_ORDER = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];

  get groupedSchedulesByDay(): { day: string; label: string; slots: RestaurantSchedule[] }[] {
    console.log("DEBUG SCHEDULES REÇUS :", JSON.parse(JSON.stringify(this.schedules)));
    const groups = new Map<string, RestaurantSchedule[]>();
    for (const s of this.schedules ?? []) {
      const d = s.day;
      if (!groups.has(d)) groups.set(d, []);
      groups.get(d)!.push(s);
    }
    for (const [, slots] of groups) {
      slots.sort((a, b) => this.timeToMinutes(a.start) - this.timeToMinutes(b.start));
    }
    return this.DAY_ORDER.map(day => ({
      day,
      label: this.getDayLabel(day),
      slots: groups.get(day) ?? []
    }));
  }

  private getDayLabel(day: string): string {
    switch (day) {
      case 'MONDAY': return 'LUNDI';
      case 'TUESDAY': return 'MARDI';
      case 'WEDNESDAY': return 'MERCREDI';
      case 'THURSDAY': return 'JEUDI';
      case 'FRIDAY': return 'VENDREDI';
      case 'SATURDAY': return 'SAMEDI';
      case 'SUNDAY': return 'DIMANCHE';
      default: return day;
    }
  }

  /** Convertit toutes les formes de temps en "HH:mm" */
  formatTime(time: any): string {
    console.log('⏱️ DEBUG formatTime input:', time);
    if (!time) return '-';
    if (typeof time === 'string') {
      if (time.includes('undefined')) return '-';
      return time.substring(0, 5);
    }
    if (typeof time === 'object') {
      const h = String(time.hour ?? 0).padStart(2, '0');
      const m = String(time.minute ?? 0).padStart(2, '0');
      return `${h}:${m}`;
    }

    return '-';
  }


  private timeToMinutes(
    time: string | { hour: number; minute: number; second?: number; nano?: number } | undefined
  ): number {
    if (!time) return 0;
    if (typeof time === 'string') {
      const [h, m] = time.split(':').map(n => parseInt(n, 10));
      return (h || 0) * 60 + (m || 0);
    }
    return (time.hour || 0) * 60 + (time.minute || 0);
  }

  onSelectSchedule(schedule: RestaurantSchedule): void {
    this.selectedScheduleId = schedule.id;
    this.scheduleSelected.emit(schedule);
  }

  onEditClick(schedule: RestaurantSchedule): void {
    this.editSchedule.emit(schedule);
  }

  onDeleteClick(schedule: RestaurantSchedule): void {
    this.deleteSchedule.emit(schedule);
  }

  trackByScheduleId(_index: number, item: RestaurantSchedule): string {
    return item.id;
  }
}
