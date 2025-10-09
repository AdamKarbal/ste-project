package Account;

import Order.Order;
import Payment.Payment;
import Payment.AlreadyExistingOrder;

import java.util.HashMap;

public class AccountUser extends Account {
    private double credit;
    private HashMap<Order, Payment> orderHistory;
    public AccountUser(String name){
        super(name);
        credit=0;
        orderHistory=new HashMap<>();
    }
    public double getCredit(){
        return credit;
    }
    public void setCredit(double newCredit){
        credit+=newCredit;
    }
    public void addOrder(Order order,Payment payment){
        if(orderHistory.keySet().contains(order)) throw new AlreadyExistingOrder("La commande existe déjà");
        orderHistory.put(order,payment);
    }
}
