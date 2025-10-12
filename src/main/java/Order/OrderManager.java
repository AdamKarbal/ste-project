package Order;

import Account.AccountUser;
import Dish.Dish;
import Payment.PaymentMethod;
import Payment.PaymentService;
import Restaurant.Restaurant;

import java.util.HashMap;
import java.util.Map;

public class OrderManager {
    private final Map<AccountUser, Order> activeOrders;
    private Order order;

    public OrderManager(Order order) {
        this.order=order;
        this.activeOrders = new HashMap<>();
    }

    public Order createOrder(AccountUser client, Restaurant restaurant) {
        if (activeOrders.containsKey(client)) {
            throw new IllegalStateException("L'utilisateur a déjà une commande active.");
        }
        Order order = new Order(client, restaurant);
        activeOrders.put(client, order);
        return order;
    }

    public Order getOrder(AccountUser client) {
        return activeOrders.get(client);
    }

    public void cancelOrder(AccountUser client) {
        Order order = activeOrders.get(client);
        if (order != null) {
            order.setState(OrderState.CANCELED);
            activeOrders.remove(client);
        }
    }
    public void addItem(AccountUser client, Dish dish, int quantity)
            throws OrderNotFoundException {
        Order order = activeOrders.get(client);
        if (order == null)
            throw new OrderNotFoundException("Aucune commande trouvée pour cet utilisateur.");
        order.addDish(dish, quantity);
    }

    public OrderState proceedPayement(PaymentMethod paymentMethod){
        order.setState(OrderState.COMPLETED); // à changer pour l'instant je garde pour les tests mais une autre méthode doit mettre l'OrderState à completed
        order.setState(PaymentService.getInstance().handleCompletedOrder(this.order,paymentMethod));
        return order.getState();
    }

    public void proceedPayment(AccountUser client, PaymentMethod paymentType)
            throws OrderNotFoundException {
        Order order = activeOrders.get(client);
        if (order == null)
            throw new OrderNotFoundException("Aucune commande trouvée pour cet utilisateur.");

        order.setState(OrderState.PAID);
        System.out.println("Paiement de " + order.getPrice() + "€ via " + paymentType);
        activeOrders.remove(client);
    }

    public void removeItem(AccountUser client, Dish dish) throws OrderNotFoundException {
        Order order = activeOrders.get(client);
        if (order == null) {
            throw new OrderNotFoundException("Aucune commande trouvée pour cet utilisateur.");
        }
        order.removeDish(dish);
        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
            activeOrders.remove(client);
        }
    }

    public void updateItem(AccountUser client, Dish dish, int newQuantity) throws OrderNotFoundException {
        Order order = activeOrders.get(client);
        if (order == null) {
            throw new OrderNotFoundException("Aucune commande trouvée pour cet utilisateur.");
        }

        order.updateDishQuantity(dish, newQuantity);

        if (order.getDishes().isEmpty()) {
            order.setState(OrderState.CANCELED);
            activeOrders.remove(client);
        }
    }

    public OrderState followOrder(AccountUser client) throws OrderNotFoundException {
        Order order = activeOrders.get(client);
        if (order == null) {
            throw new OrderNotFoundException("Aucune commande trouvée pour cet utilisateur.");
        }

        return order.getState();
    }

}