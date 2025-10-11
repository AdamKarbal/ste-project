package steps;

import Utils.Date;
import Utils.InvalidDate;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import org.junit.jupiter.api.Assertions;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;
import Restaurant.Enum.*;
import Dish.*;
import  Restaurant.*;

public class RestaurantFilterSteps {

    private RestaurantRepo repo;
    private RestaurantService service;
    private List<Restaurant> resultats = new ArrayList<>();

    @Before
    public void setup() {
        repo = new RestaurantRepo(new ArrayList<>());
        service = new RestaurantService(repo);
    }

    @Given("un catalogue de restaurants")
    public void un_catalogue_de_restaurants(DataTable table) {
        for(Map<String,String> row : table.asMaps()){
            String name = row.get("name");
            List<cuisineType> c = List.of(cuisineType.valueOf(row.get("cuisineType").toUpperCase()));
            establishmentType e = establishmentType.valueOf(row.get("establishmentType").toUpperCase());
            Dish d1 = new Dish(Double.parseDouble(row.get("price1")),row.get("dish1"),null,null,null,null,null);
            Dish d2 = new Dish(Double.parseDouble(row.get("price2")),row.get("dish2"),null,null,null,null,null);
            List<Dish> dishes = List.of(d1,d2);
            List<RestaurantSchedule> schedules = new ArrayList<>();
            schedules.add(makeSchedule(row.get("start1"), row.get("end1"), row.get("capacity1")));
            schedules.add(makeSchedule(row.get("start2"), row.get("end2"), row.get("capacity2")));
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

    @When("je filtre les restaurants par cuisine {string}")
    public void je_filtre_les_restaurants_par_cuisine(String cuisine) {
        cuisineType type = cuisineType.valueOf(cuisine.toUpperCase());
        resultats = service.filterByCuisine(type);
    }

    @Then("je vois exactement {int} restaurants")
    public void je_vois_exactement_restaurants(int count) {
        Assertions.assertEquals(count, resultats.size(),"Taille inattendue, il y a : " + resultats.toString() );
    }

    @And("la liste contient {string}")
    public void la_liste_contient(String name) {
        String attendu = name.trim();
        boolean present = resultats.stream()
                .map(Restaurant::getName)
                .anyMatch(n -> n != null && n.trim().equalsIgnoreCase(attendu));

        Assertions.assertTrue(
                present,
                "La liste ne contient pas " + attendu +
                        " ; présents: " + resultats.stream().map(Restaurant::getName).toList()
        );
    }

    @When("je filtre les restaurants par horaire {string}")
    public void je_filtre_par_horaire(String s) {
        // "HH:mm" → filtre à une heure précise
        if (s.matches("\\d{2}:\\d{2}")) {
            Utils.Date t = hhmm(s);
            resultats = service.filterByAvailability(t);
            return;
        }
        // "ouvert" → fallback non déterministe (voir Option B)
        if (s.trim().equalsIgnoreCase("ouvert")) {
            resultats = service.filterByAvailability();
            return;
        }
        throw new IllegalArgumentException("Format attendu: \"HH:mm\" (ex: 11:30) ou \"ouvert\"");
    }

    @When("je filtre les restaurants par etablissement {string}")
    public void je_filtre_par_etablissement(String etablissement) {
        establishmentType e = establishmentType.valueOf(etablissement.toUpperCase());
        resultats = service.filterByType(e);
    }



    // --- Helpers SCHEDULES ---

    private static RestaurantSchedule makeSchedule(String start, String end, String capStr) {
        if (start == null || end == null || start.isBlank() || end.isBlank()) {
            // pas de créneau fourni : on met un créneau neutre capacité 0
            return new RestaurantSchedule(hhmm("00:00"), hhmm("00:00"), 0);
        }
        int cap = (capStr == null || capStr.isBlank()) ? 0 : Integer.parseInt(capStr.trim());
        return new RestaurantSchedule(hhmm(start.trim()), hhmm(end.trim()), cap);
    }

    /** Convertit "HH:mm" -> Utils.Date via ton Builder. */
    private static Date hhmm(String s) {
        String[] p = s.split(":");
        int h = Integer.parseInt(p[0].trim());
        int m = Integer.parseInt(p[1].trim());
        try {
            return new Date.Builder()
                    .day(1).month(1).year(1970)   // date factice, on ne compare que les heures
                    .hour(h).minute(m)
                    .build();
        } catch (InvalidDate e) {
            throw new RuntimeException("Heure invalide: " + s, e);
        }
    }


}