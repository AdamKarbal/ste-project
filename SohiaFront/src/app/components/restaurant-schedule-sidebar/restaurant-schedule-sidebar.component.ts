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

  @Input() restaurantName: string | undefined;
  @Input() schedules: RestaurantSchedule[] = [];
  @Input() selectedScheduleId: string | undefined;

  @Output() scheduleSelected = new EventEmitter<RestaurantSchedule>();
  private readonly DAY_ORDER = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];

  get sortedSchedules(): RestaurantSchedule[] {
    if (!this.schedules) return [];
    return [...this.schedules].sort((a, b) => {
      const dayDiff =
        this.DAY_ORDER.indexOf(a.day) - this.DAY_ORDER.indexOf(b.day);
      if (dayDiff !== 0) return dayDiff;
      return this.timeToMinutes(a.start) - this.timeToMinutes(b.start);
    });
  }

  formatTime(time: any): string {
    if (!time) return '';
    if (typeof time === 'string') {
      if (time.includes('undefined')) return '';
      return time.substring(0, 5);
    }
    if (typeof time === 'object') {
      const h = String(time.hour ?? 0).padStart(2, '0');
      const m = String(time.minute ?? 0).padStart(2, '0');
      return `${h}:${m}`;
    }

    return '';
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

  trackByScheduleId(_index: number, item: RestaurantSchedule): string {
    return item.id;
  }
}
