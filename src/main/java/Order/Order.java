package Order;

import Account.AccountUser;
import Restaurant.Exception.ScheduleNoExist;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Restaurant.RestaurantScheduleManager;
import Dish.Dish;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Order {
    private static int compteur=0;
    private int id;
    private double price;
    private OrderState state;
    private final Map<Dish, Integer> dishes;
    private Restaurant restaurant;
    private AccountUser client;
    private OrderDeliveryPoint deliveryPoint;
    private LocalDateTime deliveryTime;
    private String idSchedule;

    public Order(AccountUser client,Restaurant restaurant){
        compteur++;
        id=compteur;
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
    public int getId(){
        return id;
    }

    public void setPrice(float price) { this.price = price; }

    public double getPrice() { return price; }

    public OrderDeliveryPoint getDeliveryPoint() { return deliveryPoint; }

    public void setDeliveryPoint(OrderDeliveryPoint deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public void setIdSchedule(String id){
        this.idSchedule = id;
    }

    public String getIdSchedule(){
        return idSchedule;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime){
        this.deliveryTime = deliveryTime;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public RestaurantSchedule chooseSchedule(LocalTime time) {
        RestaurantSchedule schedule = RestaurantScheduleManager
                .getAvailableScheduleFor(getRestaurant(), time)
                .orElseThrow(() -> new ScheduleNoExist("Aucun créneau disponible à cette heure : " + time));

        setIdSchedule(schedule.getId());
        setDeliveryTime(LocalDateTime.of(LocalDate.now(), time));
        return schedule;
    }

    public void setState(OrderState newState) {
        /*if (deliveryPoint == null && newState != OrderState.CREATED && newState != OrderState.CANCELED){
            throw new InvalidOrderStateException(
                    "Impossible de changer l'état " + newState + " car le point de livraison manque"
            );
        }*/
        if(this.state!=newState){
            this.state = newState;
            System.out.println("L'état de la commande a bien changé pour : " + newState);
        }
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
        /*if (dish == null || quantity <= 0) return false;

        if (!restaurant.containsDish(dish)) {
            throw new DifferentRestaurantException("Impossible d'ajouter le plat '" + dish.getName() + "' : il provient d'un autre restaurant ");
        }*/

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

    public void updatePrice() {
        price = 0f;
        for (Map.Entry<Dish, Integer> entry : dishes.entrySet()) {
            price += entry.getKey().getPrice() * entry.getValue();
        }
    }


}
