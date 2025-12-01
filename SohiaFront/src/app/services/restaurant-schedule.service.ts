import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestaurantSchedule } from '../models/restaurant-schedule.model';

export interface SchedulePayload {
  day: string;
  start: string;
  end: string;
  capacity: number;
}

@Injectable({ providedIn: 'root' })
export class RestaurantScheduleService {

  private readonly baseUrl = 'http://localhost:8007/api/schedule';

  constructor(private http: HttpClient) {}

  // GET /api/schedule/{restaurant}
  getSchedules(restaurantName: string): Observable<RestaurantSchedule[]> {
    return this.http.get<RestaurantSchedule[]>(`${this.baseUrl}/${restaurantName}`);
  }

  // POST /api/schedule/{restaurant}/add
  addSchedule(restaurantName: string, payload: SchedulePayload): Observable<{ ok: boolean }> {
    return this.http.post<{ ok: boolean }>(
      `${this.baseUrl}/${restaurantName}/add`,
      payload
    );
  }

  // PUT /api/schedule/{restaurant}/time/{id}
  updateScheduleTime(
    restaurantName: string,
    id: string,
    payload: { day: string; start: string; end: string }
  ): Observable<{ ok: boolean }> {
    return this.http.put<{ ok: boolean }>(
      `${this.baseUrl}/${restaurantName}/time/${id}`,
      payload
    );
  }

  // PUT /api/schedule/{restaurant}/capacity/{id}
  updateScheduleCapacity(
    restaurantName: string,
    id: string,
    capacity: number
  ): Observable<{ ok: boolean }> {
    return this.http.put<{ ok: boolean }>(
      `${this.baseUrl}/${restaurantName}/capacity/${id}`,
      { capacity }
    );
  }

  // DELETE /api/schedule/{restaurant}/{id}
  deleteSchedule(restaurantName: string, id: string): Observable<{ ok: boolean }> {
    return this.http.delete<{ ok: boolean }>(
      `${this.baseUrl}/${restaurantName}/${id}`
    );
  }
}
