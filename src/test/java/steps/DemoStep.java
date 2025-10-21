package steps;
import Account.AccountUser;
import Payment.PaymentMethod;
import Payment.PaymentState;
import Restaurant.*;
import Account.AccountManager;
import Restaurant.Exception.CuisineTypeInList;
import Restaurant.Exception.DishInDishes;
import Restaurant.Exception.EstablishmentTypeAlreadySet;
import Utils.InvalidDate;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import org.junit.jupiter.api.*;

import java.time.LocalTime;
import java.util.*;
import Order.Order;
import Order.OrderManager;
import Order.OrderState;


import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;
import Restaurant.Enum.*;
import Dish.*;


public class DemoStep {

    private RestaurantRepo repo;
    private RestaurantService service;
    private List<Restaurant> resultats = new ArrayList<>();
    private Map<String, AccountManager> restaurateurs = new HashMap<>();
    private RestaurantManager currentManager;
    private Restaurant currentRestaurant;
    private AccountUser client;
    private List<Restaurant> restaurantsByType;
    private Order currentOrder;
    private OrderManager currentOrderManager;
    private OrderState orderStatus;
    private RestaurantFactory factory;
    @Before
    public void setup() {

        repo = new RestaurantRepo(new ArrayList<>());
        service = new RestaurantService(repo);
        factory = new RestaurantFactory(service);
        restaurateurs = new HashMap<>();
    }

    @Given("un catalogue de restos")
    public void un_catalogue_de_restos(DataTable table) {
        for (Map<String,String> row : table.asMaps()) {
            String name = row.get("name");
            List<CuisineType> c = List.of(CuisineType.valueOf(row.get("cuisineType").toUpperCase()));
            establishmentType e = establishmentType.valueOf(row.get("establishmentType").toUpperCase());

            Dish d1 = new Dish(Double.parseDouble(row.get("price1")), row.get("dish1"), null, null, null, null);
            Dish d2 = new Dish(Double.parseDouble(row.get("price2")), row.get("dish2"), null, null, null, null);
            d1.setDishTag(new ArrayList<>(parseTags(row.get("tags1"))));
            d2.setDishTag(new ArrayList<>(parseTags(row.get("tags2"))));

            Restaurant r = new Restaurant.Builder()
                    .name(name)
                    .dishes(List.of(d1, d2))
                    .restaurantSchedules(new ArrayList<>())
                    .cuisineType(c)
                    .establishmentType(e)
                    .build();
            DayOfTheWeek[] days = DayOfTheWeek.values();
            for (DayOfTheWeek day : days) {
                RestaurantScheduleManager.addSchedule(r, day, hhmm(row.get("start1")), hhmm(row.get("end1")), Integer.parseInt(row.get("capacity1")));
                RestaurantScheduleManager.addSchedule(r, day, hhmm(row.get("start2")), hhmm(row.get("end2")), Integer.parseInt(row.get("capacity2")));
            }
            try {
                service.addRestaurant(r);
            } catch (Exception ignored) {}
        }
    }

    @Given("que le restaurant {string} propose le plat {string} à {double}€")
    public void que_le_restaurant_propose_le_plat_à_€(String string, String string2, Double double1) throws DishInDishes {
        currentRestaurant = service.getRestaurantByName(string);
        currentManager = new RestaurantManager(currentRestaurant);
        Dish dish=new Dish(double1,string2,null,null,null,null);
        Assertions.assertNotNull(currentRestaurant);
        Assertions.assertTrue(currentRestaurant.getDishes().contains(dish));
        Assertions.assertEquals(dish.getPrice(),currentRestaurant.findDish(string2,double1).getPrice());
    }

    @And("que {string} a un créneau de {int}:{int} à {int}:{int} avec une capacité de {int} repas")
    public void que_a_un_créneau_de_à_avec_une_capacité_de_repas(String string, Integer int1, Integer int2, Integer int3, Integer int4, Integer int5) {
        List<RestaurantSchedule> schedules = RestaurantScheduleManager.getTodaySchedules(currentRestaurant);
        System.out.println();
        Assertions.assertTrue(schedules.contains(new RestaurantSchedule(DayOfTheWeek.MONDAY,LocalTime.of(int1,int2),int5)));
    }

    @And("que le client {string} est connecté à son compte")
    public void que_le_client_est_connecté_à_son_compte(String string) {
        client=new AccountUser(string);
    }

    @And("son crédit est de {double}€")
    public void son_credit_est_de(double montant){
        client.setCredit(montant);
    }

    @When("{string} recherche les restaurants de type {string}")
    public void recherche_les_restaurants_de_type(String string, String string2) {
        CuisineType type = CuisineType.valueOf(string2);
        restaurantsByType = service.filterByCuisine(type);
    }

    @When("il sélectionne le restaurant {string}")
    public void il_sélectionne_le_restaurant(String string) {
        currentOrderManager= new OrderManager();
        currentOrderManager.createOrder(client,currentRestaurant);
    }

    @And("il ajoute {int} {string} à {int}€ à son panier")
    public void ilAjouteÀ€ÀSonPanier(int arg0, String arg1, int arg2) {
        Dish chosenDish = new Dish(arg2,arg1,null,null,null,null);
        currentOrderManager.addItem(chosenDish,arg0);
    }

    @When("il choisit le créneau de {int}:{int} et valide sa commande")
    public void il_choisit_le_créneau_de_et_valide_sa_commande(Integer int1, Integer int2) {
        currentOrderManager.chooseSchedule(LocalTime.of(int1,int2));
    }

    @When("il procède au paiement avec son crédit")
    public void il_procède_au_paiement_avec_son_crédit() {
        orderStatus=currentOrderManager.finishOrder(PaymentMethod.CASH);
    }

    @Then("le paiement doit être accepté et une commande payeé par cash doit être créée pour {string}")
    public void le_paiement_doit_être_accepté_et_une_commande_n_doit_être_créée_pour(String string) {
        Assertions.assertEquals(client.getOrderHistory().get(currentOrderManager.getCurrentOrder()).getStatus(), PaymentState.PAID);
    }

    @Then("l'historique de commandes de {string} doit contenir {int} commandes")
    public void l_historique_de_commandes_de_doit_contenir_la_commande_n(String name,Integer int1) {
        Assertions.assertEquals(client.getOrderHistory().size(),1);
    }

    @Then("la capacité du créneau de {int}:{int} à {int}:{int} de {string} doit être maintenant de {int} repas")
    public void la_capacité_du_créneau_de_à_de_doit_être_maintenant_de_repas(Integer int1, Integer int2, Integer int3, Integer int4, String string, Integer int5) {
        Assertions.assertEquals(RestaurantScheduleManager.getCapacityById(currentRestaurant,currentOrderManager.getCurrentOrder().getIdSchedule()),int5);
    }


    @Given("le restaurateur {string} à été enregistré")
    public void le_restaurateur_à_été_enregistré(String nomRestaurateur) {
        AccountManager account = new AccountManager(nomRestaurateur);
        restaurateurs.put(nomRestaurateur, account);
    }

    @When("{string} crée son restaurant {string}")
    public void crée_son_restaurant(String nomRestaurateur, String nomRestaurant) {
        AccountManager account = restaurateurs.get(nomRestaurateur);
        Assertions.assertNotNull(account, "Le restaurateur doit être enregistré avant de créer un restaurant");
        account.createRestaurant(nomRestaurant,factory);
        currentManager = account.getRestaurantManager();
        currentRestaurant = currentManager.getRestaurant();
        repo.addRestaurant(currentRestaurant);
        Assertions.assertNotNull(currentRestaurant, "Le restaurant n’a pas été créé correctement");
    }

    @When("Il le configure : cuisineType {string}")
    public void il_le_configure_cuisine_type(String type) throws CuisineTypeInList {
        Assertions.assertNotNull(currentManager, "Aucun restaurant en cours de configuration");
        currentManager.addCuisineType(CuisineType.valueOf(type.toUpperCase()));
    }

    @When("Il le configure : establishmentType {string}")
    public void il_le_configure_establishment_type(String type) throws EstablishmentTypeAlreadySet {
        Assertions.assertNotNull(currentManager, "Aucun restaurant en cours de configuration");
        currentManager.addEstablishmentType(establishmentType.valueOf(type.toUpperCase()));
    }

    @When("Il ajoute le plat {string} à {double}€")
    public void il_ajoute_le_plat_à_€(String nomPlat, Double prix) throws DishInDishes {
        Assertions.assertNotNull(currentManager, "Aucun restaurant en cours de configuration");
        Dish plat = new Dish(prix, nomPlat, null, null, null, null);
        currentManager.addDish(plat);
    }

    @When("Il ajoute un creneau de {int}:{int} à {int}:{int} avec {int} capacités")
    public void il_ajoute_un_creneau_de_à_avec_capacités(Integer h1, Integer m1, Integer h2, Integer m2, Integer capacite) {
        Assertions.assertNotNull(currentManager, "Aucun restaurant en cours de configuration");
        currentManager.updateSchedule(DayOfTheWeek.MONDAY,
                LocalTime.of(h1, m1),
                LocalTime.of(h2, m2),
                capacite);
    }

    @Then("Le restaurant doit être disponible sous le nom de {string}")
    public void le_restaurant_doit_être_disponible_sous_le_nom_de(String nomRestaurant) {
        boolean present = repo.getRestaurants().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(nomRestaurant));
        Assertions.assertTrue(present, "Le restaurant " + nomRestaurant + " n’a pas été trouvé dans le repo");
    }

    @Then("{string} peut passer trouver le restaurant {string}")
    public void peut_passer_trouver_le_restaurant(String nomClient, String nomRestaurant) {
        List<Restaurant> trouvés = service.findByName(nomRestaurant);
        Assertions.assertFalse(trouvés.isEmpty(), "Le client " + nomClient + " ne trouve pas le restaurant " + nomRestaurant);
    }

    @Then("{string} peut trouver le plat {string}")
    public void peut_trouver_le_plat(String nomClient, String nomPlat) {
        boolean platTrouvé = repo.getRestaurants().stream()
                .flatMap(r -> r.getDishes().stream())
                .anyMatch(d -> d.getName().equalsIgnoreCase(nomPlat));
        Assertions.assertTrue(platTrouvé, "Le client " + nomClient + " ne trouve pas le plat " + nomPlat);
    }

    // --- Helpers SCHEDULES ---
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
        // essai direct par nom d'enum (VEGAN, GLUTEN_FREE, ...)
        String candidate = raw.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try { return DishTag.valueOf(candidate); } catch (IllegalArgumentException ignore) {}

        // essai par label ("Vegan", "Gluten-free", ...) en normalisant
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
