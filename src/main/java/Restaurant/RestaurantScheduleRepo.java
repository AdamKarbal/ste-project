package Restaurant;

import java.util.List;

public class RestaurantScheduleRepo {
    private final List<RestaurantSchedule> restaurantsSchedule;

    public RestaurantScheduleRepo(List<RestaurantSchedule> restaurantsSchedule) {
        this.restaurantsSchedule = restaurantsSchedule;
    }

    public List<RestaurantSchedule> getRestaurantsSchedule() {
        return restaurantsSchedule;
    }

    public void addRestaurantSchedule(RestaurantSchedule restaurantsSchedule){
        this.restaurantsSchedule.add(restaurantsSchedule);
    }

    public boolean restaurantScheduleExist(RestaurantSchedule restaurantsSchedule){
        return this.restaurantsSchedule.contains(restaurantsSchedule);
    }
}
