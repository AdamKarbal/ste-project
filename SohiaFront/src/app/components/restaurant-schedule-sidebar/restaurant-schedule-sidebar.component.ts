import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RestaurantSchedule } from '../../models/restaurant-schedule.model';

@Component({
  selector: 'se-restaurant-schedule-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-schedule-sidebar.component.html',
  styleUrls: ['./restaurant-schedule-sidebar.component.css']
})
export class RestaurantScheduleSidebarComponent {

  /** Nom du restaurant (affiché en haut) */
  @Input() restaurantName: string | undefined;

  /** Liste brute des créneaux : on utilise le modèle global */
  @Input() schedules: RestaurantSchedule[] = [];

  /** ID du créneau actuellement sélectionné */
  @Input() selectedScheduleId: string | undefined;

  /** Événement quand un créneau est sélectionné */
  @Output() scheduleSelected = new EventEmitter<RestaurantSchedule>();

  /** Ordre officiel des jours (optionnel si tu veux trier) */
  private readonly DAY_ORDER = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];

  /** Groupe les créneaux par jour */
  get groupedSchedulesByDay(): { day: string; label: string; slots: RestaurantSchedule[] }[] {
    const groups = new Map<string, RestaurantSchedule[]>();

    for (const s of this.schedules ?? []) {
      const day = s.day;
      if (!groups.has(day)) {
        groups.set(day, []);
      }
      groups.get(day)!.push(s);
    }

    // tri par heure de début dans chaque jour, en gérant string ou objet
    for (const [, slots] of groups) {
      slots.sort((a, b) => this.timeToMinutes(a.start) - this.timeToMinutes(b.start));
    }

    // si tu veux l'ordre Lundi -> Dimanche :
    return this.DAY_ORDER.map(day => ({
      day,
      label: this.getDayLabel(day),
      slots: groups.get(day) ?? []
    }));
  }

  /** Map MONDAY, TUESDAY... -> Lundi, Mardi... */
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

  /** Format simple en "HH:mm" à partir de string ou objet LocalTime sérialisé */
  formatTime(time: any): string {
    if (!time) return '';

    // Cas string : "HH:mm" ou "HH:mm:ss"
    if (typeof time === 'string') {
      if (time.includes('undefined')) return '';
      return time.substring(0, 5);
    }

    // Cas objet { hour, minute, ... }
    if (typeof time === 'object') {
      const h = String(time.hour ?? 0).padStart(2, '0');
      const m = String(time.minute ?? 0).padStart(2, '0');
      return `${h}:${m}`;
    }

    return '';
  }

  /** Pour trier les horaires, quelle que soit leur forme */
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

  trackByScheduleId(_index: number, item: RestaurantSchedule): string {
    return item.id;
  }
}
