package Payment;

import Order.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Payment {
    private static int compteur=0;
    @JsonIgnore
    private Order order;
    private int id;
    private PaymentState status;
    private PaymentMethod paymentMethod;
    public Payment(Order newOrder,PaymentMethod paymentMethod){
        compteur++;
        order=newOrder;
        id=compteur;
        status=PaymentState.PENDING;
        this.paymentMethod=paymentMethod;
    }
    public Payment(){}
    public int getId(){
        return this.id;
    }
    @JsonIgnore
    public Order getOrder(){
        return order;
    }
    public void setStatus(PaymentState state){
        this.status=state;
    }
    public PaymentState getStatus(){
        return status;
    }
    public PaymentMethod getPaymentMethod(){
        return paymentMethod;
    }

    public PaymentState processPayment() {
        return null;
    }
}
