package Account;

import Restaurant.*;

public class AccountManager extends Account {
    RestaurantManager restaurantManager;

    public AccountManager(String name){
        super(name);
        this.restaurantManager = new RestaurantManager(name);
    }

    public RestaurantManager getRestaurantManager() {
        return restaurantManager;
    }
}
