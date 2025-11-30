package Payment;

import Order.OrderState;

public record PaymentDoneDTO(int orderId, OrderState status) {
}
