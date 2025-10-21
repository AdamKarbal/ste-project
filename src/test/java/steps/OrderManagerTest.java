package steps;
import Order.OrderManager;
import Order.OrderState;

import Account.AccountUser;
import Dish.Dish;
import Order.Order;
import Payment.Payment;
import Payment.PaymentMethod;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

public class OrderManagerTest {

    Order order;
    Order order2;
    AccountUser client;
    HashMap<Order, Payment> history;
    double creditInitial;

    // Classe interne pour stocker les données de test
    private static class TestClientData {
        public AccountUser client;
        public Order order1;
        public Order order2;
    }

    public static TestClientData createClientWithTwoPaidOrders() {
        TestClientData data = new TestClientData();
        data.client = new AccountUser("Adam");
        data.client.setCredit(3);

        data.order1 = new Order(data.client, null);
        data.order2 = new Order(data.client, null);

        Dish dish1 = new Dish(1.0, "d", "e", null, null, null);
        Dish dish2 = new Dish(2.0, "ee", "edd", null, null, null);

        data.order1.addDish(dish1, 1);
        data.order2.addDish(dish2, 1);

        new OrderManager(data.order1).proceedPayement(PaymentMethod.CASH);
        new OrderManager(data.order2).proceedPayement(PaymentMethod.ONLINE);

        return data;
    }

    @Given("l'utilisateur a passé plusieurs commandes")
    public void l_utilisateur_a_passé_plusieurs_commandes() {
        TestClientData data = createClientWithTwoPaidOrders();
        client = data.client;
        order = data.order1;
        order2 = data.order2;
    }

    @When("il veut accéder à son historique de commandes")
    public void il_veut_accéder_à_son_historique_de_commandes() {
        history = client.getOrderHistory();
    }

    @Then("toutes les commandes qu'il a réalisé et qui ont été payées s'affichent")
    public void toutes_les_commandes_qu_il_a_réalisé_et_qui_ont_été_payées_s_affichent() {
        Assertions.assertEquals(OrderState.PAID, order.getState());
        Assertions.assertEquals(OrderState.PAID, order2.getState());
        assertTrue(history.containsKey(order));
        assertTrue(history.containsKey(order2));
    }

    @And("les bons moyens de paiement s'affichent")
    public void leur_moyen_de_paiement_s_affiche() {
        assertEquals(PaymentMethod.CASH, history.get(order).getPaymentMethod());
        assertEquals(PaymentMethod.ONLINE, history.get(order2).getPaymentMethod());
    }

    @Given("un client dispose d'un crédit de {double} euros")
    public void qu_un_client_dispose_d_un_crédit_de_euros(double credit) {
        client = new AccountUser("Adam");
        client.setCredit(credit);
        creditInitial = credit;
    }

    @And("il a passé une commande de {double} euros")
    public void qu_il_a_passé_une_commande_de_euros(double montant) {
        order = new Order(client, null);
        Dish dish = new Dish(montant, "Pizza", "Délicieuse pizza", null, null, null);
        order.addDish(dish, 1);
    }

    @When("il paie la commande avec son crédit")
    public void il_paie_la_commande_avec_son_crédit() {
        new OrderManager(order).proceedPayement(PaymentMethod.CASH);
    }

    @Then("son crédit doit être débité de {double} euros")
    public void son_crédit_doit_être_débité_de_euros(double montant) {
        double creditActuel = client.getCredit();
        Assertions.assertEquals(creditInitial - montant, creditActuel, 0.001,
                "Le crédit du client n'a pas été correctement débité");
    }
}
