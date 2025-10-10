package Order;
import Account.AccountUser;
import Dish.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Payment.PaymentMethod;
import io.cucumber.java.fr.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderManagerTest {
    Order order;
    OrderManager orderManager;
    AccountUser client;
    @BeforeEach
    void setUp(){
        client=new AccountUser("Adam");
        order=new Order(client,null);
        orderManager = new OrderManager(order);
    }


    @Quand("l'utilisateur finalise sa commande et qu'il a assez d'argent en cash pour la payer")
    public void l_utilisateur_finalise_sa_commande_et_qu_il_a_assez_d_argent_pour_la_payer() {
        order.addDish(new Dish(1.0,"d","e",null,null,null,null),1);
        order.addDish(new Dish(2.0,"d","e",null,null,null,null),1);
        client.setCredit(10);
        orderManager.proceedPayement(PaymentMethod.CASH);
    }

    @Alors("le prix de la commande est retiré de son crédit")
    public void le_prix_de_la_commande_est_retiré_de_son_crédit() {
        assertTrue(client.getCredit()>0);
    }

    @Étantdonnéque("l'utilisateur a passé plusieurs commandes")
    public void l_utilisateur_a_passé_plusieurs_commandes() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Quand("il veut accéder à son historique de commandes")
    public void il_veut_accéder_à_son_historique_de_commandes() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Alors("toutes les commandes qu'il a réalisé et qui ont été payées s'affichent")
    public void toutes_les_commandes_qu_il_a_réalisé_et_qui_ont_été_payées_s_affichent() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Alors("leur moyen de paiement s'affiche")
    public void leur_moyen_de_paiement_s_affiche() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}