package Payment;

import Account.AccountUser;
import Order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class InternalPaymentTest {
    InternalPayment payment;
    AccountUser client;
    Order order;
    @BeforeEach
    void setUp(){
        client=new AccountUser("Adam");
        order=new Order(client,null);
        payment=new InternalPayment(order);
    }

    @Test
    void correctPayment(){
        client.setCredit(10);
        order.setPrice(9);
        payment.processPayment(PaymentMethod.CASH);
        assertEquals(PaymentState.PAID,payment.getStatus());
        assertEquals(client.getCredit(),1.0);
    }

    @Test
    void badPayment(){
        client.setCredit(9);
        order.setPrice(10);
        assertThrows(NotEnoughMoney.class,()->payment.processPayment(PaymentMethod.CASH));
        assertEquals(PaymentState.FAILED,payment.getStatus());
        assertEquals(client.getCredit(),9.0);
    }

}