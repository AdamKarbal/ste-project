package Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepo {
    private final List<Restaurant> restaurants;

    public RestaurantRepo(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public Restaurant addRestaurant(Restaurant resto){
        restaurants.add(resto);
        return resto;
    }

    public void updateRestaurants(List<Restaurant> restaurants){
        this.restaurants.clear();
        this.restaurants.addAll(restaurants);
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
        boolean existed = restaurants.removeIf(r -> name.equals(r.getName()));

        restaurants.add(updatedRestaurant);

        return existed;
    }

    public void clear(){
        restaurants.clear();
    }


}
