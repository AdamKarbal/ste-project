package steps;

import Account.AccountService;
import Payment.PaymentMethod;
import Payment.PaymentState;
import Account.AccountManager;
import Utils.InvalidDate;
import io.cucumber.datatable.DataTable;

import java.time.LocalTime;

import Order.Order;
import Order.OrderManager;

import Dish.Dish;
import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.establishmentType;
import Restaurant.Restaurant;
import Restaurant.RestaurantFactory;
import Restaurant.RestaurantManager;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;
import Dish.*;
import Api.Utils.Requester;
import org.junit.jupiter.api.Assertions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static steps.StartServer.catalogueInit;

public class DemoStep {
    private int currentOrderId;

    private final AccountService accountService = new AccountService();
    private final RestaurantManager manager = new RestaurantManager();
    private final Map<String, AccountManager> restaurateurs = new HashMap<>();
    private OrderManager currentOrderManager;
    private Restaurant currentRestaurant;
    private String currentRestaurantName;
    private int currentClientId;

    @Given("un catalogue de restos")
    public void un_catalogue_de_restos(DataTable table) {
        if (catalogueInit) {
            System.out.println(">>> [Given catalogue] déjà initialisé, on ne recrée pas.");
            return;
        }
        catalogueInit = true;

        for (Map<String, String> row : table.asMaps()) {
            String name = row.get("name");
            List<CuisineType> c = List.of(CuisineType.valueOf(row.get("cuisineType").toUpperCase()));
            establishmentType e = establishmentType.valueOf(row.get("establishmentType").toUpperCase());

            Dish d1 = Requester.createDish(new Dish(Double.parseDouble(row.get("price1")), row.get("dish1"), "", DishCategory.STARTER, DishType.PIZZA, null).toCreateDTO());
            Dish d2 = Requester.createDish(new Dish(Double.parseDouble(row.get("price2")), row.get("dish2"), "", DishCategory.STARTER, DishType.PIZZA, null).toCreateDTO());
            d1.setDishTags(new ArrayList<>(parseTags(row.get("tags1"))));
            d2.setDishTags(new ArrayList<>(parseTags(row.get("tags2"))));

            Restaurant r = new Restaurant.Builder()
                    .name(name)
                    .dishes(List.of(d1, d2))
                    .restaurantSchedules(new ArrayList<>())
                    .cuisineType(c)
                    .establishmentType(e)
                    .build();

            Requester.createRestaurant(r);

            List<DayOfTheWeek> days = DayOfTheWeek.getRealDays();
            for (DayOfTheWeek day : days) {
                Requester.addSchedule(name, day, hhmm(row.get("start1")), hhmm(row.get("end1")), Integer.parseInt(row.get("capacity1")));
                Requester.addSchedule(name, day, hhmm(row.get("start2")), hhmm(row.get("end2")), Integer.parseInt(row.get("capacity2")));
            }
        }
    }

    @Given("que le restaurant {string} propose le plat {string} à {double}€")
    public void que_le_restaurant_propose_le_plat_à_€(String restoName, String dishName, Double price) {
        currentRestaurant = Requester.getRestaurantByName(restoName);

        Assertions.assertNotNull(currentRestaurant);

        Dish found = currentRestaurant.findDish(dishName, price);
        Assertions.assertNotNull(found);
        Assertions.assertEquals(price, found.getPrice());
    }

    @And("que {string} a un créneau de {int}:{int} à {int}:{int} avec une capacité de {int} repas")
    public void que_a_un_créneau_de_à_avec_une_capacité_de_repas(String restoName, Integer h1, Integer m1, Integer h2, Integer m2, Integer capacity) {
        Assertions.assertTrue(
                Requester.getSchedules(restoName).stream().anyMatch(s ->
                        s.getDay() == DayOfTheWeek.MONDAY &&
                                s.getStart().equals(LocalTime.of(h1, m1)) &&
                                s.getEnd().equals(LocalTime.of(h2, m2)) &&
                                s.getCapacity() == capacity
                ),
                "Aucun créneau correspondant trouvé côté distant"
        );

    }

    @And("que le client {string} est connecté à son compte")
    public void que_le_client_est_connecté_à_son_compte(String name) {
        currentClientId = accountService.createClient(name).getId();
        Assertions.assertNotNull(accountService.getClientById(currentClientId));
    }

    @And("son crédit est de {double}€")
    public void son_credit_est_de(double montant){
        accountService.addCredit(currentClientId,montant);
    }

    @When("{string} recherche les restaurants de type {string}")
    public void recherche_les_restaurants_de_type(String clientName, String typeStr) {
        RestaurantService service = new RestaurantService(new RestaurantRepo(new ArrayList<>()));
        CuisineType type = CuisineType.valueOf(typeStr);
        List<Restaurant> restaurantsByType = service.filterByCuisine(type);
        Assertions.assertFalse(restaurantsByType.isEmpty());
    }

    @When("il sélectionne le restaurant {string}")
    public void il_sélectionne_le_restaurant(String restoName) {
        currentOrderManager = new OrderManager();
        // Nouveau : on passe l'ID du resto sous forme de String (ici le nom)
        Order order = currentOrderManager.createOrder(currentClientId, currentRestaurant.getName());
        currentOrderId = order.getId();
    }
  
    @And("il ajoute {int} {string} à son panier")
    public void ilAjouteÀ€ÀSonPanier(int quantity, String dishName) throws Exception {
        // On récupère le vrai plat du restaurant par son nom
        Dish chosenDish = currentRestaurant.getDishes().stream()
                .filter(d -> d.getName().equalsIgnoreCase(dishName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Plat " + dishName + " introuvable dans le restaurant"));

        // On utilise son id pour l'OrderManager REST
        System.out.println("là"+chosenDish.getId()+"");
        currentOrderManager.addItem(currentOrderId, chosenDish.getId(), quantity);
        System.out.println("c bon");
    }

    @When("il procède au paiement avec son crédit")
    public void il_procède_au_paiement_avec_son_crédit() {
        System.out.println("finsihOrder");
        currentOrderManager.finishOrder(currentOrderId, PaymentMethod.CASH);
        System.out.println("c bon ?");
    }

    @Then("le paiement doit être accepté et une commande payeé par cash doit être créée pour {string}")
    public void le_paiement_doit_être_accepté_et_une_commande_n_doit_être_créée_pour(String clientName) {
        Order order = Requester.getOrderById(currentOrderId);
        Assertions.assertNotNull(order, "Order not found");
        // On vérifie que l'historique du client contient bien un paiement PAID pour cette order
        PaymentState state = order.getClient().getOrderHistory().get(currentOrderId).getStatus();
        Assertions.assertEquals(PaymentState.PAID, state);
    }

    @Then("l'historique de commandes de {string} doit contenir {int} commande")
    public void l_historique_de_commandes_de_doit_contenir_la_commande_n(String name, Integer expectedCount) {
        Order order = Requester.getOrderById(currentOrderId);
        Assertions.assertNotNull(order, "Order not found");
        Assertions.assertEquals(expectedCount.intValue(), order.getClient().getOrderHistory().size());
    }

    @Then("la capacité du créneau de {int}:{int} à {int}:{int} de {string} doit être maintenant de {int} repas")
    public void la_capacité_du_créneau_de_à_de_doit_être_maintenant_de_repas(Integer h1, Integer m1, Integer h2, Integer m2, String restoName, Integer expectedCapacity) {
        Order order = Requester.getOrderById(currentOrderId);
        Assertions.assertNotNull(order, "Order not found");

        Assertions.assertEquals(
                Requester.getScheduleCapacity(currentRestaurant.getName(), order.getIdSchedule()),
                expectedCapacity
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
        account.createRestaurant(nomRestaurant, new RestaurantFactory());

        currentRestaurantName = nomRestaurant;
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
        Requester.addSchedule(currentRestaurantName,
                DayOfTheWeek.MONDAY,
                LocalTime.of(h1, m1),
                LocalTime.of(h2, m2),
                cap);
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

        String norm = java.text.Normalizer.normalize(raw.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toUpperCase().replace(' ', '_').replace('-', '_');
        for (DishTag t : DishTag.values()) {
            String lab = java.text.Normalizer.normalize(t.getLabel(), java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}+", "").toUpperCase().replace(' ', '_').replace('-', '_');
            if (lab.equals(norm)) return t;
        }
        throw new IllegalArgumentException("Tag inconnu: " + raw);
    }

}
