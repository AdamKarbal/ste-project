package Payment;

import Order.Order;

public class ExternalPayment extends Payment{
    public ExternalPayment(Order order,PaymentMethod paymentMethod){
        super(order,paymentMethod);
    }
    public PaymentState processPayment(){
        return PaymentState.PAID;
    }
}
