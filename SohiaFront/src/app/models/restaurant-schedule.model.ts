export interface RestaurantSchedule {
  id: string;
  day: string;
  start: string | { hour: number; minute: number; second?: number; nano?: number };
  end:   string | { hour: number; minute: number; second?: number; nano?: number };
  capacity: number;
}

