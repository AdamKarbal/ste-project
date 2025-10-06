package Restaurant;

import Dish.Dish;
import Dish.DishType;

import java.util.List;

public class Restaurant {
    private String name;
    private List<Dish> dishes;
    private DishType dishType;
    private RestaurantType restaurantType;
    private RestaurantAvailability restaurantAvailability;
    private RestaurantCapacityStatus restaurantCapacityStatus;

    public Restaurant(String name) {
        this.name = name;
    }
}
