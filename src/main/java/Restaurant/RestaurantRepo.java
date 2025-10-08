package Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepo {
    private List<Restaurant> restaurants;

    public RestaurantRepo(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void addRestaurant(Restaurant resto){
        restaurants.add(resto);
    }

    public boolean restaurantExist(Restaurant resto){
        return restaurants.contains(resto);
    }
}
