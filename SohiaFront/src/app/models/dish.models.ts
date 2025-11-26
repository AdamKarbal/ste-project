export type LocalTime = [number, number];

export enum DishCategory {
  STARTER = 'STARTER',
  MAIN_COURSE = 'MAIN_COURSE',
  DESSERT = 'DESSERT',
  DRINK = 'DRINK'
}

export const DishCategoryLabel: Record<DishCategory, string> = {
  [DishCategory.STARTER]: 'Entrée',
  [DishCategory.MAIN_COURSE]: 'Plat Principal',
  [DishCategory.DESSERT]: 'Dessert',
  [DishCategory.DRINK]: 'Boisson'
};


export enum DishType {
  PASTA = 'PASTA',
  MEAT = 'MEAT',
  PIZZA = 'PIZZA',
  ICE_CREAM = 'ICE_CREAM',
  CAKE = 'CAKE',
  SALAD = 'SALAD',
  SOUP = 'SOUP',
  SEAFOOD = 'SEAFOOD',
  SANDWICH = 'SANDWICH',
  OTHER = 'OTHER'
}

export const DishTypeLabel: Record<DishType, string> = {
  [DishType.PASTA]: 'Pâtes',
  [DishType.MEAT]: 'Viande',
  [DishType.PIZZA]: 'Pizza',
  [DishType.ICE_CREAM]: 'Glace',
  [DishType.CAKE]: 'Gâteau',
  [DishType.SALAD]: 'Salade',
  [DishType.SOUP]: 'Soupe',
  [DishType.SEAFOOD]: 'Fruits de mer',
  [DishType.SANDWICH]: 'Sandwich',
  [DishType.OTHER]: 'Autre'
};

export enum DishTag {
  GLUTEN_FREE = 'GLUTEN_FREE',
  CONTAINS_FROZEN_PRODUCTS = 'CONTAINS_FROZEN_PRODUCTS',
  MAY_CONTAIN_TRACES_OF_PEANUTS = 'MAY_CONTAIN_TRACES_OF_PEANUTS',
  VEGETARIAN = 'VEGETARIAN',
  VEGAN = 'VEGAN',
  HALAL = 'HALAL',
  KOSHER = 'KOSHER',
  LACTOSE_FREE = 'LACTOSE_FREE',
  ORGANIC = 'ORGANIC',
  SPICY = 'SPICY'
}

export const DishTagLabel: Record<DishTag, string> = {
  [DishTag.GLUTEN_FREE]: 'Sans Gluten 🌾',
  [DishTag.CONTAINS_FROZEN_PRODUCTS]: 'Produits surgelés ❄️',
  [DishTag.MAY_CONTAIN_TRACES_OF_PEANUTS]: 'Traces d\'arachides 🥜',
  [DishTag.VEGETARIAN]: 'Végétarien 🥗',
  [DishTag.VEGAN]: 'Végan 🌱',
  [DishTag.HALAL]: 'Halal ☪️',
  [DishTag.KOSHER]: 'Kasher ✡️',
  [DishTag.LACTOSE_FREE]: 'Sans Lactose 🥛',
  [DishTag.ORGANIC]: 'Bio 🌿',
  [DishTag.SPICY]: 'Epicé 🌶️'
};

export interface Dish {
  name: string;
  price: number;
  description: string;
  category: DishCategory;
  type: DishType;
  dishTag: DishTag[];
  availableToppings?: any[];
  selectedToppings?: any[];
}

