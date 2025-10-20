package Payment;

import Order.Order;

public class ExternalPayment extends Payment {
    public ExternalPayment(Order newOrder){
        super(newOrder);
    }

    @Override
    public void processPayment(PaymentMethod method) {

    }
}
