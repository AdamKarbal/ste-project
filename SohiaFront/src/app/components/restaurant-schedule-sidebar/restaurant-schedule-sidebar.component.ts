import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

export interface RestaurantSchedule {
  id: string;
  day: string;      // correspond à DayOfTheWeek côté Java (MONDAY, TUESDAY, ...)
  start: string;    // ex "11:30" ou "11:30:00" selon ton API
  end: string;      // idem
  capacity: number;
}

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

  /** Liste brute des créneaux */
  @Input() schedules: RestaurantSchedule[] = [];

  /** ID du créneau actuellement sélectionné */
  @Input() selectedScheduleId: string | undefined;

  /** Événement quand un créneau est sélectionné */
  @Output() scheduleSelected = new EventEmitter<RestaurantSchedule>();

  /** Groupe les créneaux par jour */
  get groupedSchedulesByDay(): { day: string; label: string; slots: RestaurantSchedule[] }[] {
    const groups = new Map<string, RestaurantSchedule[]>();

    for (const s of this.schedules ?? []) {
      if (!groups.has(s.day)) {
        groups.set(s.day, []);
      }
      groups.get(s.day)!.push(s);
    }

    // tri par heure de début dans chaque jour
    for (const [, slots] of groups) {
      slots.sort((a, b) => a.start.localeCompare(b.start));
    }

    // conversion en tableau avec label pour l'affichage
    const result: { day: string; label: string; slots: RestaurantSchedule[] }[] = [];
    for (const [day, slots] of groups.entries()) {
      result.push({
        day,
        label: this.getDayLabel(day),
        slots
      });
    }

    // tu peux trier les jours ici si besoin (Lundi -> Dimanche)
    return result;
  }

  /** Map MONDAY, TUESDAY... -> Lundi, Mardi... (adapter à ton enum si besoin) */
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

  /** Format simple HH:mm à partir de "HH:mm" ou "HH:mm:ss" */
  formatTime(time: string | undefined): string {
    if (!time) return '';
    return time.substring(0, 5); // "11:30" à partir de "11:30:00"
  }

  onSelectSchedule(schedule: RestaurantSchedule): void {
    this.selectedScheduleId = schedule.id;
    this.scheduleSelected.emit(schedule);
  }

  trackByScheduleId(_index: number, item: RestaurantSchedule): string {
    return item.id;
  }
}
