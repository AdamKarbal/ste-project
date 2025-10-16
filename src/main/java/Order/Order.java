package Order;

import Account.AccountUser;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Dish.Dish;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private double price;
    private OrderState state;
    private final Map<Dish, Integer> dishes;
    private Restaurant restaurant;
    private AccountUser client;
    private OrderDeliveryPoint deliveryPoint;
    private LocalDateTime deliveryTime;

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

    public void setPrice(float price) { this.price = price; }

    public double getPrice() { return price; }

    public OrderDeliveryPoint getDeliveryPoint() { return deliveryPoint; }

    public void setDeliveryPoint(OrderDeliveryPoint deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public LocalDateTime getDeliveryTime() { return deliveryTime; }

    public boolean assignSchedule(RestaurantSchedule chosenSchedule) {
        if (chosenSchedule == null) return false;

        if (restaurant == null || restaurant.getRestaurantSchedules() == null) return false;

        boolean exists = restaurant.getRestaurantSchedules().stream()
                .anyMatch(s -> s.getId() == chosenSchedule.getId());
        if (!exists) return false;

        this.deliveryTime = LocalDateTime.of(
                LocalDate.now(),
                chosenSchedule.getEnd().plusMinutes(15)
        );

        System.out.println("Créneau assigné : " + chosenSchedule + " | Livraison prévue à " + deliveryTime);
        return true;
    }

    public void setState(OrderState newState) {
        if (deliveryPoint == null && newState != OrderState.CREATED && newState != OrderState.CANCELED){
            throw new InvalidOrderStateException(
                    "Impossible de changer l'état " + newState + " car le point de livraison manque"
            );
        }
        this.state = newState;
        System.out.println("L'état de la commande a bien changé pour : " + newState);
    }

    public boolean confirmOrder() {
        if (deliveryPoint == null) {
            System.err.println("Impossible de confirmer : aucun point de livraison défini.");
            return false;
        }

        if (deliveryTime == null) {
            System.err.println("Impossible de confirmer : aucun créneau horaire sélectionné.");
            return false;
        }

        try {
            setState(OrderState.PAID);
            return true;
        } catch (InvalidOrderStateException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }


    public Map<Dish, Integer> getDishes() {
        return dishes;
    }

    public boolean addDish(Dish dish, int quantity) throws DifferentRestaurantException {
        if (dish == null || quantity <= 0) return false;

        if (!restaurant.containsDish(dish)) {
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
