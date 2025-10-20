package steps;

import Account.AccountManager;
import Restaurant.Exception.CuisineTypeInList;
import Restaurant.Exception.DishInDishes;
import Restaurant.Exception.EstablishmentTypeAlreadySet;
import Utils.InvalidDate;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import org.junit.jupiter.api.Assertions;

import java.time.LocalTime;
import java.util.*;

import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;
import Restaurant.Enum.*;
import Dish.*;
import  Restaurant.*;

public class DemoStep {

    private RestaurantRepo repo;
    private RestaurantService service;
    private List<Restaurant> resultats = new ArrayList<>();
    private Map<String, AccountManager> restaurateurs = new HashMap<>();
    private RestaurantManager currentManager;
    private Restaurant currentRestaurant;

    @Before
    public void setup() {
        repo = new RestaurantRepo(new ArrayList<>());
        service = new RestaurantService(repo);
    }

    @Given("un catalogue de restos")
    public void un_catalogue_de_restos(DataTable table) {
        for(Map<String,String> row : table.asMaps()){
            String name = row.get("name");
            List<cuisineType> c = List.of(cuisineType.valueOf(row.get("cuisineType").toUpperCase()));
            establishmentType e = establishmentType.valueOf(row.get("establishmentType").toUpperCase());
            List<DishTag> t1 = parseTags(row.get("tags1"));
            List<DishTag> t2 = parseTags(row.get("tags2"));
            Dish d1 = new Dish(Double.parseDouble(row.get("price1")),row.get("dish1"),null,null,null,null);
            Dish d2 = new Dish(Double.parseDouble(row.get("price2")),row.get("dish2"),null,null,null,null);
            d1.setDishTag(new ArrayList<>(t1));
            d2.setDishTag(new ArrayList<>(t2));
            List<Dish> dishes = List.of(d1,d2);
            List<RestaurantSchedule> schedules = new ArrayList<>();
            schedules.add(makeSchedule(DayOfTheWeek.MONDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.MONDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.TUESDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.TUESDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.WEDNESDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.WEDNESDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.THURSDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.THURSDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.FRIDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.FRIDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            schedules.add(makeSchedule(DayOfTheWeek.SATURDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.SUNDAY, row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(DayOfTheWeek.SUNDAY, row.get("start2"), row.get("end2"), row.get("capacity2")));
            Restaurant r = new Restaurant.Builder()
                    .name(name)
                    .dishes(dishes)
                    .restaurantSchedules(schedules)
                    .cuisineType(c)
                    .establishmentType(e)
                    .build();
            service.addRestaurant(r);
        }
    }

    @Given("que le restaurant {string} propose le plat {string} à {double}€")
    public void que_le_restaurant_propose_le_plat_à_€(String string, String string2, Double double1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("que {string} a un créneau de {int}:{int} à {int}:{int} avec une capacité de {int} repas")
    public void que_a_un_créneau_de_à_avec_une_capacité_de_repas(String string, Integer int1, Integer int2, Integer int3, Integer int4, Integer int5) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("que le client {string} est connecté à son compte")
    public void que_le_client_est_connecté_à_son_compte(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("{string} recherche les restaurants de type {string}")
    public void recherche_les_restaurants_de_type(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("il sélectionne le restaurant {string}")
    public void il_sélectionne_le_restaurant(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("il ajoute {int} {string} à son panier")
    public void il_ajoute_à_son_panier(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("il choisit le créneau de {int}:{int} et valide sa commande")
    public void il_choisit_le_créneau_de_et_valide_sa_commande(Integer int1, Integer int2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("il procède au paiement avec son crédit")
    public void il_procède_au_paiement_avec_son_crédit() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("il a assez sur son compte")
    public void il_a_assez_sur_son_compte() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("le paiement doit être accepté et une commande N°{int} doit être créée pour {string}")
    public void le_paiement_doit_être_accepté_et_une_commande_n_doit_être_créée_pour(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("l'historique de commandes de {string} doit contenir la commande N°{int}")
    public void l_historique_de_commandes_de_doit_contenir_la_commande_n(String string, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("la capacité du créneau de {int}:{int} à {int}:{int} de {string} doit être maintenant de {int} repas")
    public void la_capacité_du_créneau_de_à_de_doit_être_maintenant_de_repas(Integer int1, Integer int2, Integer int3, Integer int4, String string, Integer int5) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("le restaurateur de {string} doit voir la commande N°{int} dans sa liste de commandes à préparer")
    public void le_restaurateur_de_doit_voir_la_commande_n_dans_sa_liste_de_commandes_à_préparer(String string, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
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
        currentManager = new RestaurantManager(nomRestaurant);
        currentRestaurant = currentManager.getRestaurant();
        repo.addRestaurant(currentRestaurant);
        Assertions.assertNotNull(currentRestaurant, "Le restaurant n’a pas été créé correctement");
    }

    @When("Il le configure : cuisineType {string}")
    public void il_le_configure_cuisine_type(String type) throws CuisineTypeInList {
        Assertions.assertNotNull(currentManager, "Aucun restaurant en cours de configuration");
        currentManager.addCuisineType(cuisineType.valueOf(type.toUpperCase()));
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

    private static RestaurantSchedule makeSchedule(DayOfTheWeek day, String start, String end, String capStr) {
        if (start == null || end == null || start.isBlank() || end.isBlank()) {
            // pas de créneau fourni : on met un créneau neutre capacité 0
            return new RestaurantSchedule(day, hhmm("00:00"), hhmm("00:00"), 0);
        }
        int cap = (capStr == null || capStr.isBlank()) ? 0 : Integer.parseInt(capStr.trim());
        return new RestaurantSchedule(day, hhmm(start.trim()), hhmm(end.trim()), cap);
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
