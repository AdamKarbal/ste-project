import {Dish} from './dish.models';

export interface DishCreateResponse extends Dish{
  id:number;
}
