package Order;

import Account.AccountUser;
import Api.Utils.Requester;
import Restaurant.Exception.ScheduleNoExist;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Restaurant.RestaurantScheduleManager;
import Dish.Dish;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    private Map<Integer, Integer> dishes;
    @JsonProperty("restaurant")
    private String restaurantId;
    private AccountUser client;
    private OrderDeliveryPoint deliveryPoint;
    private LocalDateTime deliveryTime;
    private String idSchedule;
    private final ObjectMapper mapper = new ObjectMapper();
    public Order(AccountUser client,String restaurantId){
        compteur++;
        id=compteur;
        price=0;
        state=OrderState.CREATED;
        this.dishes=new HashMap<>();
        this.restaurantId= restaurantId;
        this.client=client;
    }
    public Order(){}



    public String getRestaurant() {

        return restaurantId;
    }

    public OrderState getState(){
        return state;
    }
    public AccountUser getClient(){
        return null;
    }
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
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

    /*public RestaurantSchedule chooseSchedule(LocalTime time) {
        RestaurantSchedule schedule = RestaurantScheduleManager
                .getAvailableScheduleFor(getRestaurant(), time)
                .orElseThrow(() -> new ScheduleNoExist("Aucun créneau disponible à cette heure : " + time));

        setIdSchedule(schedule.getId());
        setDeliveryTime(LocalDateTime.of(LocalDate.now(), time));
        return schedule;
    }*/

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


    public Map<Integer, Integer> getDishes() {
        return dishes;
    }


    public boolean addDish(int dishId, int quantity) {
        /*if (dish == null || quantity <= 0) return false;

        if (!restaurant.containsDish(dish)) {
            throw new DifferentRestaurantException("Impossible d'ajouter le plat '" + dish.getName() + "' : il provient d'un autre restaurant ");
        }*/
        dishes.put(dishId, dishes.getOrDefault(dishId, 0) + quantity);
        updatePrice();
        setState(OrderState.IN_PREPARATION);
        return true;
    }

    public void removeDish(int dishId) {
        if (dishes.containsKey(dishId)) {
            dishes.remove(dishId);
            updatePrice();

            if (dishes.isEmpty()) {
                setState(OrderState.CREATED);
            }
        }
    }

    public void updateDishQuantity(int dishId, int newQuantity) {
        if (newQuantity <= 0) {
            removeDish(dishId);
        } else if (dishes.containsKey(dishId)) {
            dishes.put(dishId, newQuantity);
        }
        updatePrice();
    }

    public void updatePrice() {
        price = 0;
        for (Map.Entry<Integer, Integer> entry : dishes.entrySet()) {
            int dishId = entry.getKey();
            int quantity = entry.getValue();

            try {
                Dish dish = Requester.getDishById(dishId);
                price += dish.getPrice() * quantity;
            } catch (Exception e) {
                System.err.println("Impossible de récupérer le plat " + dishId);
            }
        }
    }

    @Override
    public String toString(){
        return "Commande id "+getId();
    }


}
