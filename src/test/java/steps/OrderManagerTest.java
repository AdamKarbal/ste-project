package steps;

import Account.AccountUser;
import Order.Order;
import Order.OrderManager;
import Payment.Payment;
import Payment.PaymentMethod;
import Api.Utils.Requester;
import io.cucumber.java.en.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrderManagerTest {

    Order order;
    Order order1;
    Order order2;
    AccountUser client;
    Map<Integer, Payment> history;
    double creditInitial;
    OrderManager orderManager = new OrderManager();

    private static class TestClientData {
        public AccountUser client;
        public Order order1;
        public Order order2;
    }

    private TestClientData createClientWithTwoPaidOrdersUsingRequester() {
        TestClientData data = new TestClientData();

        AccountUser localClient = new AccountUser("Adam");
        localClient.setCredit(50.0);
        data.client = Requester.createUser(localClient);

        data.order1 = orderManager.createOrder(data.client.getId(), null);
        data.order1.setPrice(1.0);

        data.order2 = orderManager.createOrder(data.client.getId(), null);
        data.order2.setPrice(2.0);

        orderManager.finishOrder(data.order1.getId(), PaymentMethod.CASH);
        orderManager.finishOrder(data.order2.getId(), PaymentMethod.ONLINE);

        return data;
    }

    @Given("l'utilisateur a passé plusieurs commandes")
    public void l_utilisateur_a_passé_plusieurs_commandes() {
        TestClientData data = createClientWithTwoPaidOrdersUsingRequester();
        client = data.client;
        order = data.order1;
        order2 = data.order2;
    }

    @When("il veut accéder à son historique de commandes")
    public void il_veut_accéder_à_son_historique_de_commandes() {
        AccountUser clientFromApi = Requester.getClientById(client.getId());
        history = clientFromApi.getOrderHistory();
    }

    @Then("toutes les commandes qu'il a réalisé et qui ont été payées s'affichent")
    public void toutes_les_commandes_qu_il_a_réalisé_et_qui_ont_été_payées_s_affichent() {
        assertNotNull(history);
        assertEquals(2, history.size());
        assertTrue(history.containsKey(order.getId()));
        assertTrue(history.containsKey(order2.getId()));
    }

    @And("les bons moyens de paiement s'affichent")
    public void leur_moyen_de_paiement_s_affiche() {
        assertNotNull(history.get(order.getId()));
        assertNotNull(history.get(order2.getId()));
        assertEquals(PaymentMethod.CASH, history.get(order.getId()).getPaymentMethod());
        assertEquals(PaymentMethod.ONLINE, history.get(order2.getId()).getPaymentMethod());
    }

    @Given("un client dispose d'un crédit de {double} euros")
    public void qu_un_client_dispose_d_un_crédit_de_euros(double credit) {
        AccountUser localClient = new AccountUser("Adam");
        localClient.setCredit(credit);
        client = Requester.createUser(localClient);
        creditInitial = credit;
        System.out.println(client.getId());
    }

    @And("il a passé une commande de {double} euros")
    public void qu_il_a_passé_une_commande_de_euros(double montant) {
        order1 = new Order(client.getId(), null);
        order1.setPrice(montant);
        order1 = Requester.createOrder(order1);
    }

    @When("il paie la commande avec son crédit")
    public void il_paie_la_commande_avec_son_crédit() {
        orderManager.finishOrder(order1.getId(), PaymentMethod.CASH);
    }

    @Then("son crédit doit être débité de {double} euros")
    public void son_crédit_doit_être_débité_de_euros(double montant) {
        AccountUser clientFromApi = Requester.getClientById(client.getId());
        double creditActuel = clientFromApi.getCredit();
        assertEquals(creditInitial - montant, creditActuel, 0.001);
    }
}
