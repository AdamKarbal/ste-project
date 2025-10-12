package Order;

import Account.AccountUser;
import Dish.Dish;
import Payment.Payment;
import Payment.PaymentMethod;
import io.cucumber.java.fr.*;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class OrderManagerTest {

    Order order;
    Order order2;
    AccountUser client;
    HashMap<Order, Payment> history;

    public static TestClientData createClientWithTwoPaidOrders() {
        TestClientData data = new TestClientData();
        data.client = new AccountUser("Adam");

        data.order1 = new Order(data.client, null);
        data.order2 = new Order(data.client, null);

        Dish dish1 = new Dish(1.0, "d", "e", null, null, null, null);
        Dish dish2 = new Dish(2.0, "ee", "edd", null, null, null, null);

        data.order1.addDish(dish1, 1);
        data.order2.addDish(dish2, 1);

        new OrderManager(data.order1).proceedPayement(PaymentMethod.CASH);
        new OrderManager(data.order2).proceedPayement(PaymentMethod.ONLINE);

        return data;
    }

    @Étantdonné("l'utilisateur a passé plusieurs commandes")
    public void l_utilisateur_a_passé_plusieurs_commandes() {
        TestClientData data = createClientWithTwoPaidOrders();
        client = data.client;
        order = data.order1;
        order2 = data.order2;
    }

    @Quand("il veut accéder à son historique de commandes")
    public void il_veut_accéder_à_son_historique_de_commandes() {
        history = client.getOrderHistory();
    }

    @Alors("toutes les commandes qu'il a réalisé et qui ont été payées s'affichent")
    public void toutes_les_commandes_qu_il_a_réalisé_et_qui_ont_été_payées_s_affichent() {
        assertEquals(OrderState.PAID, order.getState());
        assertEquals(OrderState.PAID, order2.getState());
        assertTrue(history.containsKey(order));
        assertTrue(history.containsKey(order2));
    }

    @Alors("les bons moyens de paiement s'affichent")
    public void leur_moyen_de_paiement_s_affiche() {
        assertEquals(PaymentMethod.CASH, history.get(order).getPaymentMethod());
        assertEquals(PaymentMethod.ONLINE, history.get(order2).getPaymentMethod());
    }
}

class TestClientData {
    public AccountUser client;
    public Order order1;
    public Order order2;
}