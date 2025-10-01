package Restaurant;

import java.util.List;

public class RestaurantFilter {
    RestaurantRepo restaurantRepo;
    public RestaurantFilter(RestaurantRepo restaurantRepo) {
        this.restaurantRepo = restaurantRepo;
    }

    public List<Restaurant> filterByType(establishmentType type) {

    }
}
