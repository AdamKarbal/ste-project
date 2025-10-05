package Restaurant;

import Utils.Date;

import java.util.List;

public class RestaurantFilter {
    RestaurantRepo restaurantRepo;
    public RestaurantFilter(RestaurantRepo restaurantRepo) {
        this.restaurantRepo = restaurantRepo;
    }

    public List<Restaurant> filterByType(establishmentType type) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant -> restaurant.getEstablishmentType().equals(type))
                .toList() ;
    }

    public List<Restaurant> filterByAvailibility() {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(Restaurant::isOpenNow)
                .toList();
    }

    public List<Restaurant> filterByAvailibility(Date targetTime) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(r -> r.isOpenAt(targetTime))
                .toList();
    }
}
