package Order;

import Account.AccountUser;
import Dish.Dish;
import Payment.Payment;
import Payment.PaymentMethod;
import Payment.PaymentService;
import Payment.PaymentState;
import Restaurant.Restaurant;
import Dish.DishTopping;


public class OrderManager {

    private PaymentService paymentService;
    private Order currentOrder;
    private final CompletedOrderManager completedOrderManager;

    public OrderManager(PaymentService paymentService,CompletedOrderManager completedOrderManager) {
        this.currentOrder = null;
        this.paymentService = paymentService;
        this.completedOrderManager = completedOrderManager;
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

    public void proceedPayment(PaymentMethod method) throws OrderNotFoundException {
        if (currentOrder == null)
            throw new OrderNotFoundException("Aucune commande active à payer.");

        Payment payment = paymentService.processPayment(currentOrder, method);

        if (payment.getStatus() == PaymentState.PAID) {
            currentOrder.setState(OrderState.PAID);
            System.out.println("Paiement réussi via " + method + " pour " + currentOrder.getPrice() + "€");
            completedOrderManager.archiveOrder(currentOrder);

            // On libère la commande active
            currentOrder = null;
        } else {
            currentOrder.setState(OrderState.CANCELED);
            System.out.println("Échec du paiement via " + method);
        }
    }

    public OrderState followOrder() {
        Order order = getCurrentOrder();
        return order.getState();
    }

    public void addToppingToDish(Dish dish, DishTopping topping) {
        Order order = getCurrentOrder();
        if (!order.getDishes().containsKey(dish)) {
            throw new IllegalArgumentException("Le plat n'existe pas dans la commande actuelle.");
        }
        dish.addTopping(topping);
        order.updatePrice();
    }
}
