package Account;

import Order.Order;
import Payment.Payment;
import Payment.AlreadyExistingOrder;
import Restaurant.Restaurant;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class AccountUser extends Account {
    private double credit;
    private HashMap<Integer, Payment> orderHistory;
    public AccountUser(String name){
        super(name);
        credit=0;
        orderHistory=new HashMap<>();
    }
    public AccountUser(){
        super();
        credit=0;
        orderHistory=new HashMap<>();
    }
    public double getCredit(){
        return credit;
    }
    public void setCredit(double newCredit){
        credit=newCredit;
    }
    public void registerOrder(int orderId,Payment payment){
        if(orderHistory.keySet().contains(orderId)) throw new AlreadyExistingOrder("La commande existe déjà");
        orderHistory.put(orderId,payment);
    }
    public Map<Integer, Payment> getOrderHistory() {
        return Map.copyOf(orderHistory);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AccountUser other = (AccountUser) obj;
        return name != null && id==other.id;
    }
}
