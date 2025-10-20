package Order;

import Account.AccountUser;
import Dish.Dish;
import Restaurant.Restaurant;

public class OrderManager {

    private Order currentOrder;

    public OrderManager() {
        this.currentOrder = null;
    }


    public Order createOrder(AccountUser client, Restaurant restaurant) {
        if (currentOrder != null && currentOrder.getState() != OrderState.CANCELED) {
            throw new IllegalStateException("Une commande est déjà en cours.");
        }
        currentOrder = new Order(client, restaurant);
        return currentOrder;
    }

    public Order getCurrentOrder() {
        if (currentOrder == null) {
            throw new OrderNotFoundException("Aucune commande active trouvée.");
        }
        return currentOrder;
    }

    public void cancelOrder() {
        if (currentOrder == null) {
            throw new OrderNotFoundException("Aucune commande active à annuler.");
        }
        currentOrder.setState(OrderState.CANCELED);
        currentOrder = null;
    }


    public void addItem(Dish dish, int quantity) {
        Order order = getCurrentOrder();
        order.addDish(dish, quantity);
    }

    public void removeItem(Dish dish) {
        Order order = getCurrentOrder();
        order.removeDish(dish);

        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
            currentOrder = null;
        }
    }

    public void updateItem(Dish dish, int newQuantity) {
        Order order = getCurrentOrder();
        order.updateDishQuantity(dish, newQuantity);

        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
            currentOrder = null;
        }
    }

    public void proceedPayment(String paymentType) {
        Order order = getCurrentOrder();
        order.setState(OrderState.PAID);
        System.out.println("Paiement de " + order.getPrice() + "€ via " + paymentType);
        currentOrder = null;
    }

    public OrderState followOrder() {
        Order order = getCurrentOrder();
        return order.getState();
    }
}
