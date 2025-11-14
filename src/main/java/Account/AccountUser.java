package Account;

import Order.Order;
import Payment.Payment;
import Payment.AlreadyExistingOrder;

import java.util.HashMap;

public class AccountUser extends Account {
    private double credit;
    private HashMap<Integer, Payment> orderHistory;
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
    public void registerOrder(int orderId,Payment payment){
        if(orderHistory.keySet().contains(orderId)) throw new AlreadyExistingOrder("La commande existe déjà");
        orderHistory.put(orderId,payment);
    }
    public HashMap<Integer,Payment> getOrderHistory(){
        return orderHistory;
    }
}
