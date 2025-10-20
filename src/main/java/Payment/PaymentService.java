package Payment;

import Order.Order;

public class PaymentService {

    public PaymentService() {}

    public Payment processPayment(Order order, PaymentMethod method) {
        if (order == null) {
            throw new IllegalArgumentException("La commande ne peut pas être nulle.");
        }

        Payment payment = createPayment(order, method);
        payment.processPayment(method);

        return payment;
    }

    private Payment createPayment(Order order, PaymentMethod method) {
        switch (method) {
            case INTERNAL:
                return new InternalPayment(order);
            case EXTERNAL:
                return new ExternalPayment(order);
            default:
                throw new UnsupportedOperationException("Méthode de paiement non supportée : " + method);
        }
    }
}
