export interface Dish {
  price: number;
  name: string;
  description: string;
  category: any;   // si tu veux je peux te créer les enums TS
  type: any;
  dishTag?: any[];
  availableToppings?: any[];
  selectedToppings?: any[];
}
