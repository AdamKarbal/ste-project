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
    private OrderDeliveryPoint deliveryPoint;

    public Order(AccountUser client, Restaurant restaurant) {
        this.price = 0;
        this.state = OrderState.CREATED;
        this.dishes = new ArrayList<>();
        this.restaurant = restaurant;
        this.client = client;
        this.deliveryPoint = null;
    }

    public AccountUser getClient() { return client; }

    public void setPrice(float price) { this.price = price; }

    public float getPrice() { return price; }

    public OrderState getState() { return state; }

    public OrderDeliveryPoint getDeliveryPoint() { return deliveryPoint; }

    public void setDeliveryPoint(OrderDeliveryPoint deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public void setState(OrderState newState) {
        if (deliveryPoint == null && newState != OrderState.CREATED) {
            throw new InvalidOrderStateException(
                    "Impossible de changer l'état " + newState + " car le point de livraison manque"
            );
        }
        this.state = newState;
        System.out.println("L'état de la commande a bien changé pour : " + newState);
    }
}
