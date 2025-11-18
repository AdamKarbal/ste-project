package Payment;
import Api.Utils.Requester;
import Order.Order;
import Order.OrderState;

public final class PaymentService {
    private static PaymentService instance;
    private PaymentService(){}
    public static PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }
    public OrderState handleCompletedOrder(Order order,PaymentMethod paymentWay) {
        if (order == null || order.getState()!= OrderState.COMPLETED) {
            throw new IllegalArgumentException("Order invalide.");
        }
        Payment payment;
        switch(paymentWay){
            case CASH:
                payment = new InternalPayment(order);
                break;
            default:
                payment = new ExternalPayment(order,paymentWay);
                break;

        }
        PaymentState paymentState=payment.processPayment();
        if(paymentState.equals(PaymentState.PAID)){
            addHistory(order,payment);
            return OrderState.PAID;
        }
        return OrderState.PAYMENT_FAILED;
    }

    private void addHistory(Order order,Payment payment){
        order.getClient().registerOrder(order.getId(),payment);
        Requester.registerOrder(order.getClientId(), order.getId(), payment);
    }

}
