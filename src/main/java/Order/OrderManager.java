package Order;

import Account.AccountUser;
import Api.Utils.Requester;
import Dish.Dish;
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




    public Order createOrder(AccountUser client, String restaurantId) { // api faite mais manque la ref au service du client
        Order order = new Order(client, restaurantId);
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

    public OrderState proceedPayement(int orderId,PaymentMethod paymentMethod){
        Order order = Requester.getOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        OrderState newState = PaymentService.getInstance().handleCompletedOrder(order, paymentMethod);
        order.setState(newState);
        return newState;
    }

    /*public void chooseSchedule(int orderId,LocalTime time){
        require(orderId).chooseSchedule(time);
    }*/

    /*public OrderState finishOrder(int orderId,PaymentMethod paymentMethod){
        Order order = require(orderId);
        OrderState status = proceedPayement(orderId,paymentMethod);
        if(RestaurantScheduleManager.checkCapacity(order.getRestaurant(),order.getIdSchedule())){
            RestaurantScheduleManager.incrementCapacityById(order.getRestaurant(), order.getIdSchedule(), -1);
            return status;
        }
        return status;
    }*/

    public void addItem(int orderId,int dishId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantité invalide.");
        Order order = Requester.getOrderById(orderId);
        order.addDish(dishId, quantity);
        Requester.updateOrder(orderId, order);
    }

    public void removeItem(int orderId,int dishId) {
        Order order = Requester.getOrderById(orderId);
        order.removeDish(dishId);
        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
        }
        Requester.updateOrder(orderId,order);
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

