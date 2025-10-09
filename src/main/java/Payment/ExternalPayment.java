package Payment;

import Order.Order;

public class ExternalPayment extends Payment{
    public ExternalPayment(Order order){
        super(order);
    }
    public PaymentState processPayment(PaymentMethod paymentMethod){
        return PaymentState.PAID;
    }
}
