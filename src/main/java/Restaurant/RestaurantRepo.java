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

    public Restaurant getRestaurantByName(String name) {
        return restaurants.stream()
                .filter(r -> r.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean updateRestaurant(String name, Restaurant updatedRestaurant) {
        Restaurant oldRestaurant = getRestaurantByName(name);
        if (oldRestaurant != null) {
            restaurants.remove(oldRestaurant);
            restaurants.add(updatedRestaurant);
            return true;
        }
        return false;
    }
}
