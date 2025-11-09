package Order;

import Account.AccountUser;
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

import java.time.LocalTime;
import java.util.Optional;


public class OrderManager {
    private OrderRepo repo;

    public OrderManager(OrderRepo repo) {
        this.repo=repo;
    }

    private Order require(int orderId) {
        return repo.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Commande " + orderId + " introuvable.")
        );
    }


    public Order createOrder(AccountUser client, Restaurant restaurant) {
        Order order = new Order(client, restaurant);
        return repo.save(order);
    }

    public void cancelOrder(int orderId) {
        Order order = require(orderId);
        order.setState(OrderState.CANCELED);
    }

    public OrderState proceedPayement(int orderId,PaymentMethod paymentMethod){
        Order order = require(orderId);
        order.setState(OrderState.COMPLETED);
        OrderState newState = PaymentService.getInstance().handleCompletedOrder(order, paymentMethod);
        order.setState(newState);
        return newState;
    }

    public void chooseSchedule(int orderId,LocalTime time){
        require(orderId).chooseSchedule(time);
    }

    public OrderState finishOrder(int orderId,PaymentMethod paymentMethod){
        Order order = require(orderId);
        OrderState status = proceedPayement(orderId,paymentMethod);
        if(RestaurantScheduleManager.checkCapacity(order.getRestaurant(),order.getIdSchedule())){
            RestaurantScheduleManager.incrementCapacityById(order.getRestaurant(), order.getIdSchedule(), -1);
            return status;
        }
        return status;
    }

    public void addItem(int orderId,Dish dish, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantité invalide.");
        Order order = require(orderId);
        order.addDish(dish, quantity);
    }

    public void removeItem(int orderId,Dish dish) {
        Order order = require(orderId);
        order.removeDish(dish);
        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
        }
    }

    public void updateItem(int orderId,Dish dish, int newQuantity) {
        if (newQuantity < 0) throw new IllegalArgumentException("Quantité négative interdite.");
        Order order = require(orderId);
        order.updateDishQuantity(dish, newQuantity);

        if (newQuantity == 0) {
            order.removeDish(dish);
        } else {
            order.updateDishQuantity(dish, newQuantity);
        }
        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
        }
    }


    public OrderState followOrder(int orderId) {
        return require(orderId).getState();
    }

    public void addToppingToDish(int orderId,Dish dish, DishTopping topping) {
        Order order = require(orderId);
        if (!order.getDishes().containsKey(dish)) {
            throw new IllegalArgumentException("Le plat n'existe pas dans cette commande.");
        }
        if (!dish.getAvailableToppings().contains(topping)) {
            throw new IllegalArgumentException("Topping non disponible pour ce plat.");
        }
        dish.addSelectedTopping(topping);
        order.updatePrice();

    }
}

