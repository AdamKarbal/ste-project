package Order;

import java.util.ArrayList;
import java.util.List;

public class CompletedOrderManager {
    private final List<Order> completedOrders;

    public CompletedOrderManager() {
        this.completedOrders = new ArrayList<>();
    }

    public void archiveOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Commande nulle !");
        completedOrders.add(order);
    }

    public List<Order> getCompletedOrders() {
        return new ArrayList<>(completedOrders);
    }
}
