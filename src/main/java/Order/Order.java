package Order;

import Account.AccountUser;
import Restaurant.Restaurant;
import Dish.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private float price;
    private OrderState state;
    private final Map<Dish, Integer> dishes;
    private Restaurant restaurant;
    private AccountUser client;
    public Order(AccountUser client,Restaurant restaurant){
        price=0;
        state=OrderState.CREATED;
        this.dishes = new HashMap<>();
        this.restaurant= restaurant;
        this.client=client;
    }


    public Restaurant getRestaurant() {
        return restaurant;
    }

    public OrderState getState(){
        return state;
    }
    public AccountUser getClient(){
        return client;
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
    public void setState(OrderState state){
        this.state=state;
    }

    public Map<Dish, Integer> getDishes() {
        return dishes;
    }
    public boolean addDish(Dish dish, int quantity) throws DifferentRestaurantException {
        if (dish == null || quantity <= 0) return false;

        if (!dish.getRestaurant().equals(this.restaurant)) {
            throw new DifferentRestaurantException("Impossible d'ajouter le plat '" + dish.getName() + "' : il provient d'un autre restaurant ");
        }

        dishes.put(dish, dishes.getOrDefault(dish, 0) + quantity);
        updatePrice();
        setState(OrderState.IN_PREPARATION);
        return true;
    }
    public void removeDish(Dish dish) {
        if (dishes.containsKey(dish)) {
            dishes.remove(dish);
            updatePrice();
            if (dishes.isEmpty()) {
                setState(OrderState.CREATED);
            }
        }
    }

    public void updateDishQuantity(Dish dish, int newQuantity) {
        if (dishes.containsKey(dish)) {
            if (newQuantity <= 0) {
                removeDish(dish);
            } else {
                dishes.put(dish, newQuantity);
                updatePrice();
            }
        }
    }

    private void updatePrice() {
        price = 0f;
        for (Map.Entry<Dish, Integer> entry : dishes.entrySet()) {
            price += entry.getKey().getPrice() * entry.getValue();
        }
    }


}
