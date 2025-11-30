import {Dish} from './dish.models';

export class DishModel implements Dish {
  price: number;
  name: string;
  description: string;
  category: string;
  type: string;
  dishTag?: string[];
  availableToppings?: string[];
  selectedToppings?: string[];
  id : number;

  constructor(data: Dish) {
    this.price = data.price;
    this.name = data.name;
    this.description = data.description;
    this.category = data.category;
    this.type = data.type;
    this.dishTag = data.dishTag ?? [];
    this.availableToppings = data.availableToppings ?? [];
    this.selectedToppings = data.selectedToppings ?? [];
    this.id=0;
  }


  toCreateJson() {
    return {
      price: this.price,
      name: this.name,
      description: this.description,
      category: this.category,
      type: this.type
    };
  }

  setId(newId : number){
    this.id = newId;
  }
}

