package Restaurant;

import Restaurant.Exception.RestaurantExist;

import java.util.ArrayList;

public class RestaurantFactory {
    private final RestaurantService restaurantService;

    public RestaurantFactory(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public Restaurant createRestaurant(String name) {
        return new Restaurant.Builder()
                .name(name)
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .cuisineType(new ArrayList<>())
                .establishmentType(null)
                .build();
    }
}