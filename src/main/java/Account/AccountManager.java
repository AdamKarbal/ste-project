package Account;

import Restaurant.*;

public class AccountManager extends Account {
    RestaurantManager restaurantManager;

    public AccountManager(String name){
        super(name);
    }

    public void createRestaurant(String restaurantName, RestaurantFactory restaurantFactory){
        this.restaurantManager = restaurantFactory.createRestaurant(restaurantName);
    }

    public RestaurantManager getRestaurantManager() {
        return restaurantManager;
    }
}
