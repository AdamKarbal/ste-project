import { CuisineType } from "./cuisine-type.model";
import { Dish } from "./dish.models";
import { EstablishmentType } from "./establishment-type.model";
import {RestaurantSchedule} from './restaurant-schedule.model';

export interface Restaurant {
  name: string;
  dishes: Dish[];
  restaurantSchedules: RestaurantSchedule[];
  cuisineTypes: CuisineType[];
  establishmentType: EstablishmentType;
}
