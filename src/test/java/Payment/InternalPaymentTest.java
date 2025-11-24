package Payment;

import Account.AccountUser;
import Api.AccountRepo.AccountRepoServer;
import Api.Order.OrderServer;
import Api.OrderRepo.OrderRepoServer;
import Api.Utils.Requester;
import Order.Order;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InternalPaymentTest {

    InternalPayment payment;
    AccountUser client;
    Order order;
    static HttpServer accountRepo;
    static HttpServer orderRepo;
    static HttpServer orderHandler;

    @BeforeAll
    public static void setUpServers() throws IOException {
        try{
        accountRepo = AccountRepoServer.startServer();
        orderRepo = OrderRepoServer.startServer();
        orderHandler = OrderServer.startServer();}
        catch (java.net.BindException e) {
            System.out.println("⚠️ InternalPaymentTest: Port 8005 déjà occupé, on continue...");
        }
    }

    @AfterAll
    public static void stopServers() {
        accountRepo.stop(0);
        orderRepo.stop(0);
        orderHandler.stop(0);
    }

    @BeforeEach
    void setUp() {
        client = new AccountUser("Adam");
        client = Requester.createUser(client);

        order = new Order(client.getId(), null);
        order.setPrice(9.0);
        order = Requester.createOrder(order);

        payment = new InternalPayment(order);
    }

    @Test
    void correctPayment() {
        Requester.updateCredit(client.getId(), 10.0);

        payment.processPayment();

        assertEquals(PaymentState.PAID, payment.getStatus());

        AccountUser clientFromApi = Requester.getClientById(client.getId());
        assertEquals(1.0, clientFromApi.getCredit(), 0.001);
    }

    @Test
    void badPayment() {
        Requester.updateCredit(client.getId(), 5.0);

        //assertThrows(NotEnoughMoney.class, () -> payment.processPayment());
        payment.processPayment();
        assertEquals(PaymentState.FAILED, payment.getStatus());

        AccountUser clientFromApi = Requester.getClientById(client.getId());
        assertEquals(5.0, clientFromApi.getCredit(), 0.001);
    }
}
