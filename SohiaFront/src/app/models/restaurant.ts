import {DishModel} from "./DishModel";
import {RestaurantSchedule} from "./restaurant-schedule.model";

export interface Restaurant {
    name: string;
    dishes: DishModel[];
    restaurantSchedules?: RestaurantSchedule[];
}
