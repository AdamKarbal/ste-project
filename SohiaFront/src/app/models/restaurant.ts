import {DishModel} from "./DishModel";
import {RestaurantSchedule} from "./restaurant-schedule.model";
import {Dish} from './dish.models';

export interface RestaurantCarte {
    name: string;
    dishes: Dish[];
    restaurantSchedules?: RestaurantSchedule[];
}
