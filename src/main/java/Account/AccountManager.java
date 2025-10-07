package Account;

import Restaurant.*;

public class AccountManager {
    RestaurantManager restaurantManager;

    public AccountManager(String name){
        this.restaurantManager = new RestaurantManager(name);
    }

    public RestaurantManager getRestaurantManager(){
        return restaurantManager;
    }
}
