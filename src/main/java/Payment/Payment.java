package Payment;

import Order.Order;

public abstract class Payment {
    private static int compteur=0;
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
    public int getId(){
        return this.id;
    }
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
    public abstract PaymentState processPayment();
}
