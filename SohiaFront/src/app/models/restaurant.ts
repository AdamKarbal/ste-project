import {DishModel} from "./DishModel";
import {RestaurantSchedule} from "./restaurant-schedule.model";

export interface RestaurantCarte {
    name: string;
    dishes: DishModel[];
    restaurantSchedules?: RestaurantSchedule[];
}
