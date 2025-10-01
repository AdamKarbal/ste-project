package Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepo {
    private List<Restaurant> restaurants;

    public RestaurantRepo() {
        restaurants = new ArrayList<Restaurant>();
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }
}
