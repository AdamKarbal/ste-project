package steps;

import Account.AccountUser;
import Dish.*;
import Order.*;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Restaurant.RestaurantScheduleManager;
import io.cucumber.java.Before;
import io.cucumber.java.fr.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrderStepDefinitions {

    private AccountUser client;
    private Restaurant restaurant;
    private Restaurant otherRestaurant;
    private Order order;
    private Exception thrownException;

    // --- Hooks ------------------------------------------------

    @Before
    public void resetContext() {
        thrownException = null;
    }

    // --- GIVEN ------------------------------------------------

    @Étantdonné("un restaurant nommé {string}")
    public void unRestaurantNomé(String name) {
        restaurant = new Restaurant.Builder()
                .name(name)
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .build();
    }

    @Étantdonné("que ce restaurant propose les plats suivants :")
    public void queCeRestaurantProposeLesPlatsSuivants(io.cucumber.datatable.DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String nom = row.get("Nom du plat");
            DishType type = DishType.valueOf(row.get("Type"));
            DishCategory category = DishCategory.valueOf(row.get("Catégorie"));
            DishTag tag = DishTag.valueOf(row.get("Tag"));
            double prix = Double.parseDouble(row.get("Prix"));
            Dish dish = new Dish(prix, nom, "Plat " + tag, category, type, List.of(tag));
            restaurant.addDish(dish);
        }
    }

    @Étantdonné("qu’un client nommé {string} est enregistré sur la plateforme")
    public void quUnClientNomé(String name) {
        client = new AccountUser(name);
    }

    @Étantdonné("que le restaurant {string} contient bien ces plats dans sa carte")
    public void queLeRestaurantContientBienCesPlatsDansSaCarte(String name) {
        assertEquals(name, restaurant.getName());
        assertFalse(restaurant.getDishes().isEmpty());
    }

    @Étantdonné("qu’Adam démarre une nouvelle commande au restaurant {string}")
    public void quAdamDémarreUneNouvelleCommande(String restaurantName) {
        order = new Order(client, restaurant);
    }

    @Étantdonné("qu’un autre restaurant nommé {string} existe")
    public void quUnAutreRestaurantNoméExiste(String name) {
        otherRestaurant = new Restaurant.Builder()
                .name(name)
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .build();
    }

    @Étantdonné("que {string} propose le plat {string}")
    public void queRestaurantProposeLePlat(String restName, String dishName) {
        // On ajoute un plat dans l’autre restaurant
        Dish lasagna = new Dish(15.0, dishName, "Lasagnes maison",
                DishCategory.MAIN_COURSE, DishType.PASTA, List.of(DishTag.VEGAN));
        otherRestaurant.addDish(lasagna);
    }

    @Étantdonné("qu’Adam a déjà une commande active au restaurant {string}")
    public void quAdamADejaUneCommandeActive(String restName) {
        order = new Order(client, restaurant);
    }

    @Étantdonné("qu’Adam a ajouté {string} et {string} dans sa commande")
    public void quAdamAAjoutéEtDansSaCommande(String dish1, String dish2) throws Exception {
        // 🔹 Initialiser la commande si elle n'existe pas encore
        if (order == null) {
            order = new Order(client, restaurant);
        }

        Dish d1 = restaurant.getDishes().stream()
                .filter(d -> d.getName().equalsIgnoreCase(dish1))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Plat introuvable : " + dish1));

        Dish d2 = restaurant.getDishes().stream()
                .filter(d -> d.getName().equalsIgnoreCase(dish2))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Plat introuvable : " + dish2));

        order.addDish(d1, 1);
        order.addDish(d2, 1);
    }

    @Étantdonné("qu’Adam a une commande en cours avec les plats du restaurant {string}")
    public void quAdamAUneCommandeEnCours(String restaurantName) throws Exception {
        order = new Order(client, restaurant);
        for (Dish d : restaurant.getDishes()) {
            order.addDish(d, 1); // 2 plats -> prix cumulé
        }
    }

    @Étantdonné("que le point de livraison {string} est défini")
    public void queLePointDeLivraisonEstDefini(String point) {
        order.setDeliveryPoint(OrderDeliveryPoint.valueOf(point));
    }

    @Étantdonné("qu’un créneau de livraison valide est disponible entre 12h00 et 12h30")
    public void quUnCreneauDeLivraisonValideEstDisponible() {
        var today = RestaurantScheduleManager.convertDayOfWeek(LocalDate.now().getDayOfWeek());
        LocalTime start = LocalTime.of(12, 0);
        LocalTime end   = LocalTime.of(12, 30);
        RestaurantScheduleManager.addSchedule(restaurant, today, start, end, 10);

        RestaurantSchedule schedule = restaurant.getRestaurantSchedules().get(0);
        boolean ok = order.assignSchedule(schedule);
        assertTrue(ok, "Le créneau doit pouvoir être assigné");
    }

    @Étantdonné("qu’Adam a une commande en cours avec un créneau horaire choisi")
    public void quAdamAUneCommandeEnCoursAvecUnCreneauHoraireChoisi() {
        var today = RestaurantScheduleManager.convertDayOfWeek(LocalDate.now().getDayOfWeek());
        LocalTime start = LocalTime.of(12, 0);
        LocalTime end   = LocalTime.of(12, 30);
        RestaurantScheduleManager.addSchedule(restaurant, today, start, end, 10);

        order = new Order(client, restaurant);
        RestaurantSchedule schedule = restaurant.getRestaurantSchedules().get(0);
        boolean ok = order.assignSchedule(schedule);
        assertTrue(ok);
    }

    // --- WHEN -------------------------------------------------

    @Quand("Adam ajoute le plat {string} en {int} exemplaire")
    public void adamAjouteLePlat(String dishName, Integer quantity) throws Exception {
        Dish dish = restaurant.getDishes().stream()
                .filter(d -> d.getName().equalsIgnoreCase(dishName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Plat introuvable : " + dishName));
        order.addDish(dish, quantity);
    }

    @Quand("Adam essaie d’ajouter le plat {string} de {string}")
    public void adamEssaieDAjouterLePlatDe(String dishName, String restaurantName) {
        try {
            Dish dish = otherRestaurant.getDishes().stream()
                    .filter(d -> d.getName().equalsIgnoreCase(dishName))
                    .findFirst().orElseThrow();
            order.addDish(dish, 1); // doit lever DifferentRestaurantException
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Quand("Adam met à jour la quantité de {string} à {int}")
    public void adamMetAJourLaQuantitéDe(String dishName, Integer quantity) {
        Dish dish = restaurant.getDishes().stream()
                .filter(d -> d.getName().equalsIgnoreCase(dishName))
                .findFirst()
                .orElse(null);
        order.updateDishQuantity(dish, quantity);
    }

    @Quand("Adam supprime {string} de la commande")
    public void adamSupprimeDeLaCommande(String dishName) {
        Dish dish = restaurant.getDishes().stream()
                .filter(d -> d.getName().equalsIgnoreCase(dishName))
                .findFirst()
                .orElse(null);
        order.removeDish(dish);
    }

    @Quand("Adam confirme sa commande")
    public void adamConfirmeSaCommande() {
        order.confirmOrder();
    }

    @Quand("Adam essaie de confirmer sa commande sans point de livraison")
    public void adamEssaieDeConfirmerSansPointDeLivraison() {
        order.confirmOrder();
    }

    // --- THEN -------------------------------------------------

    @Alors("la commande doit contenir {int} plats au total")
    public void laCommandeDoitContenirPlatsAuTotal(Integer expected) {
        assertEquals(expected, order.getDishes().size());
    }

    @Alors("le prix total de la commande doit être de {double} euros")
    public void lePrixTotalDeLaCommandeDoitEtreDe(Double expected) {
        assertEquals(expected, order.getPrice(), 0.01);
    }

    @Alors("l’état de la commande doit passer à {string}")
    public void letatDeLaCommandeDoitPasserA(String state) {
        assertEquals(OrderState.valueOf(state), order.getState());
    }

    @Alors("une erreur {string} doit être levée")
    public void uneErreurDoitEtreLevee(String exceptionName) {
        assertNotNull(thrownException, "Aucune exception capturée");
        assertEquals(exceptionName, thrownException.getClass().getSimpleName());
    }

    @Alors("le nombre de plats dans la commande reste de {int}")
    public void leNombreDePlatsReste(Integer expected) {
        assertEquals(expected, order.getDishes().size());
    }

    @Alors("le prix total reste de {double} euros")
    public void lePrixTotalResteDe(Double expected) {
        assertEquals(expected, order.getPrice(), 0.01);
    }

    @Alors("la commande ne contient plus qu’un seul plat")
    public void laCommandeNeContientPlusQuUnPlat() {
        assertEquals(1, order.getDishes().size());
    }

    @Alors("la commande doit être validée")
    public void laCommandeDoitEtreValidée() {
        assertEquals(OrderState.PAID, order.getState());
    }

    @Alors("l’état de la commande devient {string}")
    public void letatDeLaCommandeDevient(String state) {
        assertEquals(OrderState.valueOf(state), order.getState());
    }

    @Alors("la commande ne doit pas être validée")
    public void laCommandeNeDoitPasEtreValidee() {
        assertNotEquals(OrderState.PAID, order.getState());
    }

    @Alors("l’état reste inchangé")
    public void letatResteInchange() {
        assertEquals(OrderState.CREATED, order.getState());
    }

    @Alors("l’heure de livraison prévue doit être définie après le créneau choisi")
    public void lHeureDeLivraisonPrevueDoitEtreDefinieApresLeCreneauChoisi() {
        assertNotNull(order.getDeliveryTime(), "Heure de livraison non définie");
        var end = restaurant.getRestaurantSchedules().get(0).getEnd();
        assertTrue(order.getDeliveryTime().toLocalTime().isAfter(end),
                "La livraison doit être après la fin du créneau");
    }
    @Alors("le prix total doit être de {double} euros")
    public void lePrixTotalDoitEtreDe(Double expected) {
        assertEquals(expected, order.getPrice(), 0.01);
    }
}
