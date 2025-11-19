package steps;

import Account.AccountManager;
import Account.AccountUser;
import Api.RestaurantRepo.RestaurantRepoServer;
import Dish.Dish;
import Dish.DishTag;
import Order.Order;
import Order.OrderManager;
import Order.OrderRepo;
import Payment.PaymentMethod;
import Payment.PaymentState;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.establishmentType;
import Restaurant.Restaurant;
import Restaurant.RestaurantFactory;
import Restaurant.RestaurantManager;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantSchedule;
import Restaurant.RestaurantScheduleManager;
import Restaurant.RestaurantService;
import Utils.InvalidDate;
import com.sun.net.httpserver.HttpServer;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.BindException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoStep {

    private static HttpServer server;
    private final int PORT = 8002;
    private static boolean isServerRunning = false;

    private RestaurantManager manager;
    private OrderRepo orderRepo;

    private RestaurantRepo localDummyRepo;
    private RestaurantService localDummyService;
    private RestaurantFactory factory;
    private String currentRestaurantName;
    private Restaurant currentRestaurantState;
    private int currentOrderid;
    private OrderManager currentOrderManager;
    private Map<String, AccountManager> restaurateurs = new HashMap<>();
    private AccountUser client;

    @Before
    public void setup() throws IOException {
        if (!isServerRunning) {
            try {
                server = RestaurantRepoServer.startServer(PORT);
                isServerRunning = true;
            } catch (BindException e) {
                System.out.println("Serveur déjà lancé sur le port " + PORT + "");
                isServerRunning = true;
            }
        }
        orderRepo = new OrderRepo();
        manager = new RestaurantManager();
        restaurateurs = new HashMap<>();
        localDummyRepo = new RestaurantRepo(new ArrayList<>());
        localDummyService = new RestaurantService(localDummyRepo);
        factory = new RestaurantFactory(localDummyService);
    }

    @After
    public void tearDown() {
    }

    @Given("un catalogue de restos")
    public void un_catalogue_de_restos(DataTable table) throws Exception {
        for (Map<String, String> row : table.asMaps()) {
            String name = row.get("name");
            List<CuisineType> c = List.of(CuisineType.valueOf(row.get("cuisineType").toUpperCase()));
            establishmentType e = establishmentType.valueOf(row.get("establishmentType").toUpperCase());

            Dish d1 = new Dish(Double.parseDouble(row.get("price1")), row.get("dish1"), null, null, null, null);
            Dish d2 = new Dish(Double.parseDouble(row.get("price2")), row.get("dish2"), null, null, null, null);
            d1.setDishTags(new ArrayList<>(parseTags(row.get("tags1"))));
            d2.setDishTags(new ArrayList<>(parseTags(row.get("tags2"))));

            Restaurant r = new Restaurant.Builder()
                    .name(name)
                    .dishes(List.of(d1, d2))
                    .restaurantSchedules(new ArrayList<>())
                    .cuisineType(c)
                    .establishmentType(e)
                    .build();

            List<DayOfTheWeek> days = DayOfTheWeek.getRealDays();
            for (DayOfTheWeek day : days) {
                RestaurantScheduleManager.addSchedule(r, day, hhmm(row.get("start1")), hhmm(row.get("end1")), Integer.parseInt(row.get("capacity1")));
                RestaurantScheduleManager.addSchedule(r, day, hhmm(row.get("start2")), hhmm(row.get("end2")), Integer.parseInt(row.get("capacity2")));
            }

            manager.saveRestaurant(r);
        }
    }

    @Given("que le restaurant {string} propose le plat {string} à {double}€")
    public void que_le_restaurant_propose_le_plat_à_€(String nomResto, String nomPlat, Double prix) {
        currentRestaurantName = nomResto;
        currentRestaurantState = manager.getRestaurantFromRepo(nomResto);
        Assertions.assertNotNull(currentRestaurantState, "Le restaurant n'a pas été trouvé sur le serveur");
        boolean found = currentRestaurantState.getDishes().stream()
                .anyMatch(d -> d.getName().equals(nomPlat) && d.getPrice() == prix);
        Assertions.assertTrue(found, "Le plat " + nomPlat + " n'a pas été trouvé");
    }

    @And("que {string} a un créneau de {int}:{int} à {int}:{int} avec une capacité de {int} repas")
    public void que_a_un_créneau_de_à_avec_une_capacité_de_repas(String nomResto, Integer h1, Integer m1, Integer h2, Integer m2, Integer cap) {
        currentRestaurantState = manager.getRestaurantFromRepo(nomResto);
        List<RestaurantSchedule> schedules = RestaurantScheduleManager.getSchedules(currentRestaurantState);

        boolean match = schedules.stream().anyMatch(s ->
                s.getDay() == DayOfTheWeek.MONDAY &&
                        s.getStart().equals(LocalTime.of(h1, m1)) &&
                        s.getCapacity() == cap
        );
        Assertions.assertTrue(match, "Créneau non trouvé pour " + nomResto);
    }

    @And("que le client {string} est connecté à son compte")
    public void que_le_client_est_connecté_à_son_compte(String string) {
        client = new AccountUser(string);
    }

    @And("son crédit est de {double}€")
    public void son_credit_est_de(double montant) {
        client.setCredit(montant);
    }

    @When("{string} recherche les restaurants de type {string}")
    public void recherche_les_restaurants_de_type(String string, String string2) {
    }

    @When("il sélectionne le restaurant {string}")
    public void il_sélectionne_le_restaurant(String nomResto) {
        currentRestaurantName = nomResto;
        currentRestaurantState = manager.getRestaurantFromRepo(nomResto);
        currentOrderManager = new OrderManager(orderRepo);
        Order order = currentOrderManager.createOrder(client, currentRestaurantState);
        currentOrderid = order.getId();
    }

    @And("il ajoute {int} {string} à {int}€ à son panier")
    public void ilAjouteÀ€ÀSonPanier(int qte, String nomPlat, int prix) {
        Dish chosenDish = new Dish(prix, nomPlat, null, null, null, null);
        currentOrderManager.addItem(currentOrderid, chosenDish, qte);
    }

    @When("il choisit le créneau de {int}:{int} et valide sa commande")
    public void il_choisit_le_créneau_de_et_valide_sa_commande(Integer h, Integer m) {
        currentOrderManager.chooseSchedule(currentOrderid, LocalTime.of(h, m));
    }

    @When("il procède au paiement avec son crédit")
    public void il_procède_au_paiement_avec_son_crédit() {
        currentOrderManager.finishOrder(currentOrderid, PaymentMethod.CASH);
    }

    @Then("le paiement doit être accepté et une commande payeé par cash doit être créée pour {string}")
    public void le_paiement_doit_être_accepté(String nom) {
        Order order = orderRepo.findById(currentOrderid)
                .orElseThrow(() -> new AssertionError("Order not found"));
        Assertions.assertEquals(PaymentState.PAID, client.getOrderHistory().get(order).getStatus());
    }

    @Then("l'historique de commandes de {string} doit contenir {int} commandes")
    public void l_historique_de_commandes_de_doit_contenir_la_commande_n(String name, Integer count) {
        Assertions.assertEquals(count, client.getOrderHistory().size());
    }

    @Then("la capacité du créneau de {int}:{int} à {int}:{int} de {string} doit être maintenant de {int} repas")
    public void la_capacité_du_créneau_doit_être(Integer h1, Integer m1, Integer h2, Integer m2, String nomResto, Integer cap) {
        Order order = orderRepo.findById(currentOrderid).orElseThrow();
        Assertions.assertEquals(
                cap,
                RestaurantScheduleManager.getCapacityById(currentRestaurantState, order.getIdSchedule())
        );
    }

    @Given("le restaurateur {string} à été enregistré")
    public void le_restaurateur_à_été_enregistré(String nomRestaurateur) {
        AccountManager account = new AccountManager(nomRestaurateur);
        restaurateurs.put(nomRestaurateur, account);
    }

    @When("{string} crée son restaurant {string}")
    public void crée_son_restaurant(String nomRestaurateur, String nomRestaurant) {
        AccountManager account = restaurateurs.get(nomRestaurateur);
        Assertions.assertNotNull(account);
        account.createRestaurant(nomRestaurant, this.factory);

        currentRestaurantName = nomRestaurant;
        currentRestaurantState = manager.getRestaurantFromRepo(nomRestaurant);
    }

    @When("Il le configure : cuisineType {string}")
    public void il_le_configure_cuisine_type(String type) throws Exception {
        manager.addCuisineType(CuisineType.valueOf(type.toUpperCase()), currentRestaurantName);
    }

    @When("Il le configure : establishmentType {string}")
    public void il_le_configure_establishment_type(String type) throws Exception {
        manager.addEstablishmentType(establishmentType.valueOf(type.toUpperCase()), currentRestaurantName);
    }

    @When("Il ajoute le plat {string} à {double}€")
    public void il_ajoute_le_plat_à_€(String nomPlat, Double prix) throws Exception {
        Dish plat = new Dish(prix, nomPlat, null, null, null, null);
        manager.addDish(plat, currentRestaurantName);
    }

    @When("Il ajoute un creneau de {int}:{int} à {int}:{int} avec {int} capacités")
    public void il_ajoute_un_creneau(Integer h1, Integer m1, Integer h2, Integer m2, Integer cap) {
        manager.updateSchedule(DayOfTheWeek.MONDAY,
                LocalTime.of(h1, m1),
                LocalTime.of(h2, m2),
                cap, currentRestaurantName);
    }

    @Then("Le restaurant doit être disponible sous le nom de {string}")
    public void le_restaurant_doit_être_disponible(String nomRestaurant) {
        try {
            Restaurant r = manager.getRestaurantFromRepo(nomRestaurant);
            Assertions.assertNotNull(r);
            Assertions.assertEquals(nomRestaurant, r.getName());
        } catch (Exception e) {
            Assertions.fail("Le restaurant n'a pas été trouvé sur le serveur");
        }
    }

    @Then("{string} peut passer trouver le restaurant {string}")
    public void peut_passer_trouver_le_restaurant(String nomClient, String nomRestaurant) {
        try {
            Restaurant r = manager.getRestaurantFromRepo(nomRestaurant);
            Assertions.assertNotNull(r, "Restaurant introuvable sur le serveur");
            Assertions.assertEquals(nomRestaurant, r.getName());
        } catch (Exception e) {
            Assertions.fail("Le client " + nomClient + " n'a pas trouvé le restaurant " + nomRestaurant);
        }
    }

    @Then("{string} peut trouver le plat {string}")
    public void peut_trouver_le_plat(String nomClient, String nomPlat) {
        Assertions.assertNotNull(currentRestaurantName, "Aucun restaurant contextuel défini");
        try {
            Restaurant r = manager.getRestaurantFromRepo(currentRestaurantName);
            boolean platTrouve = r.getDishes().stream()
                    .anyMatch(d -> d.getName().equalsIgnoreCase(nomPlat));
            Assertions.assertTrue(platTrouve, "Le client " + nomClient + " ne trouve pas le plat " + nomPlat);
        } catch (Exception e) {
            Assertions.fail("Erreur lors de la récupération du restaurant");
        }
    }

    // --- Helpers ---

    private static LocalTime hhmm(String s) {
        String[] p = s.split(":");
        int h = Integer.parseInt(p[0].trim());
        int m = Integer.parseInt(p[1].trim());
        try {
            return LocalTime.of(h, m);
        } catch (InvalidDate e) {
            throw new RuntimeException("Heure invalide: " + s, e);
        }
    }

    private static List<DishTag> parseTags(String cell) {
        if (cell == null || cell.isBlank()) return List.of();
        return Arrays.stream(cell.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(DemoStep::toTag)
                .toList();
    }

    private static DishTag toTag(String raw) {
        String candidate = raw.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try { return DishTag.valueOf(candidate); } catch (IllegalArgumentException ignore) {}
        throw new IllegalArgumentException("Tag inconnu: " + raw);
    }
}