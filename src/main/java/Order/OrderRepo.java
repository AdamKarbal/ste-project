package Order;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OrderRepo {
    private Map<Integer,Order> orders;
    public OrderRepo(){
        orders=new HashMap<Integer, Order>();
    }
    public Optional<Order> findById(int id){
        return Optional.ofNullable(orders.get(id));
    }
    public Order save(Order order){
        orders.put(order.getId(),order);
        return order;
    }

    public boolean deleteById(int id){
        if(orders.containsKey(id)){
            orders.remove(id);
            return true;
        }
        return false;
    }
    public boolean updateOrder(int id, Order updatedOrder) {
        Order oldOrder = findById(id).orElseThrow(
                () -> new OrderNotFoundException("Commande " + id + " introuvable."));
        if (oldOrder!=null) {
            orders.remove(id);
            orders.put(id,updatedOrder);
            return true;
        }
        return false;
    }
}
