package Account;

import Restaurant.*;

public class AccountManager extends Account {
    RestaurantManager restaurantManager;

    public AccountManager(String name){
        super(name);
    }

    public RestaurantManager getRestaurantManager() {
        return restaurantManager;
    }
}
