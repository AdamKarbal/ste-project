import {Dish} from './dish.models';

export interface CartItem {
  dish: Dish;
  quantity: number;
}
