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

    public OrderState proceedPayement(PaymentMethod paymentMethod){
        currentOrder.setState(OrderState.COMPLETED); // à changer pour l'instant je garde pour les tests mais une autre méthode doit mettre l'OrderState à completed
        currentOrder.setState(PaymentService.getInstance().handleCompletedOrder(this.currentOrder,paymentMethod));
        return currentOrder.getState();
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


    public OrderState followOrder() {
        Order order = getCurrentOrder();
        return order.getState();
    }

    public void addToppingToDish(Dish dish, DishTopping topping) {
        Order order = getCurrentOrder();
        if (!order.getDishes().containsKey(dish)) {
            throw new IllegalArgumentException("Le plat n'existe pas dans la commande actuelle.");
        }

        if (!dish.getAvailableToppings().contains(topping)) {
            throw new IllegalArgumentException("Ce topping n'est pas proposé pour ce plat !");
        }

        dish.addSelectedTopping(topping);
        order.updatePrice();
    }
}
