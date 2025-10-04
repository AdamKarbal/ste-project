package Order;

import Account.AccountUser;
import Restaurant.Restaurant;
import Dish.Dish;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private float price;
    private OrderState state;
    private List<Dish> dishes;
    private Restaurant restaurant;
    private AccountUser client;
    public Order(AccountUser client,Restaurant restaurant){
        price=0;
        state=OrderState.CREATED;
        dishes=new ArrayList<>();
        this.restaurant= restaurant;
        this.client=client;
    }
    public AccountUser getClient(){
        return client;
    }
    public void setPrice(float price){
        this.price=price;
    }
    public float getPrice(){
        return price;
    }
}
