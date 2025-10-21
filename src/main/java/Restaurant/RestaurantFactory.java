package Restaurant;

import Restaurant.Exception.RestaurantExist;

import java.util.ArrayList;

public class RestaurantFactory {
    private final RestaurantService restaurantService;

    public RestaurantFactory(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public RestaurantManager createRestaurant(String name) throws RestaurantExist {
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(name)
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .cuisineType(new ArrayList<>())
                .establishmentType(null);

        Restaurant newRestaurant = builder.build();
        restaurantService.addRestaurant(newRestaurant);
        return new RestaurantManager(newRestaurant);
    }
}