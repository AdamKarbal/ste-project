package Order;

import Account.AccountUser;
import Api.Utils.Requester;
import Dish.Dish;
import Dish.DishNotFound;
import Payment.Payment;
import Payment.PaymentMethod;
import Payment.PaymentService;
import Payment.PaymentState;
import Payment.PaymentMethod;
import Payment.PaymentService;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Restaurant.RestaurantScheduleManager;
import Dish.DishTopping;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.Optional;


public class OrderManager {
    private ObjectMapper mapper = new ObjectMapper();

    public OrderManager() {
    }




    public Order createOrder(int clientId, String restaurantId) { // api faite mais manque la ref au service du client
        Order order = new Order(clientId, restaurantId);
        return Requester.createOrder(order);
    }

    public Order createOrder(int clientId,String restaurantId,double price){
        Order order = new Order(clientId,restaurantId,price);
        return Requester.createOrder(order);
    }

    public Order cancelOrder(int orderId) { // api faite
        Order order = Requester.getOrderById(orderId);
        if (order!=null){
            order.setState(OrderState.CANCELED);
            Requester.deleteOrder(orderId);
        }
        return order;
    }

    public OrderState proceedPayement(Order order,PaymentMethod paymentMethod){
        order.setState(OrderState.COMPLETED);
        OrderState newState = PaymentService.getInstance().handleCompletedOrder(order, paymentMethod);
        order.setState(newState);
        Requester.updateOrder(order.getId(),order);
        return newState;
    }

    public void chooseSchedule(int orderId,LocalTime time){
        Requester.getOrderById(orderId).chooseSchedule(time);
    }

    public OrderState finishOrder(int orderId,PaymentMethod paymentMethod){ // faudra appeler le service restaurant schedule
        Order order = Requester.getOrderById(orderId);
        OrderState status = proceedPayement(order,paymentMethod);
        /*Restaurant restaurant = Requester.getRestaurantByName(order.getRestaurant());
        if(RestaurantScheduleManager.checkCapacity(restaurant,order.getIdSchedule())){
            RestaurantScheduleManager.incrementCapacityById(restaurant, order.getIdSchedule(), -1);
            return status;
        }*/
        return status;
    }

    public boolean addItem(int orderId,int dishId, int quantity)
            throws IOException, InterruptedException, DishNotFound {

        if (quantity <= 0) throw new IllegalArgumentException("Quantité invalide.");
        Dish requestedDish = Requester.getDishById(dishId);
        if(requestedDish==null){
            throw new DishNotFound("Le plat demandé n'existe pas");
        }
        Order order = Requester.getOrderById(orderId);
        boolean completed = order.addDish(dishId, quantity);
        if(completed){
            Requester.updateOrder(orderId, order);
            return true;
        }
        return false;
    }


    public boolean removeItem(int orderId,int dishId) {
        Order order = Requester.getOrderById(orderId);
        if(order==null) return false;
        order.removeDish(dishId);
        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
        }
        Requester.updateOrder(orderId,order);
        return true;
    }

    public void updateItem(int orderId,int dishId, int newQuantity) {
        if (newQuantity < 0) throw new IllegalArgumentException("Quantité négative interdite.");
        Order order = Requester.getOrderById(orderId);
        order.updateDishQuantity(dishId, newQuantity);

        if (newQuantity == 0) {
            order.removeDish(dishId);
        } else {
            order.updateDishQuantity(dishId, newQuantity);
        }
        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
        }
        Requester.updateOrder(orderId,order);
    }


    public OrderState followOrder(int orderId) {
        return Requester.getOrderById(orderId).getState();
    }

    /*public void addToppingToDish(int orderId,int dishId, DishTopping topping) {
        Order order = require(orderId);
        if (!order.getDishes().containsKey(dishId)) {
            throw new IllegalArgumentException("Le plat n'existe pas dans cette commande.");
        }
        if (!dishId.getAvailableToppings().contains(topping)) {
            throw new IllegalArgumentException("Topping non disponible pour ce plat.");
        }
        dish.addSelectedTopping(topping);
        order.updatePrice();
        updateOrder(orderId,order);
    }*/
}

