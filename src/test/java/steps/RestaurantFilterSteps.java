package steps;

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
            List<DishTag> t1 = parseTags(row.get("tags1"));
            List<DishTag> t2 = parseTags(row.get("tags2"));
            Dish d1 = new Dish(Double.parseDouble(row.get("price1")),row.get("dish1"),null,null,null,null);
            Dish d2 = new Dish(Double.parseDouble(row.get("price2")),row.get("dish2"),null,null,null,null);
            d1.setDishTag(new ArrayList<>(t1));
            d2.setDishTag(new ArrayList<>(t2));
            List<Dish> dishes = List.of(d1,d2);
            List<RestaurantSchedule> schedules = new ArrayList<>();

            Restaurant r = new Restaurant.Builder()
                    .name(name)
                    .dishes(dishes)
                    .restaurantSchedules(schedules)
                    .cuisineType(c)
                    .establishmentType(e)
                    .build();

            DayOfTheWeek[] days = DayOfTheWeek.values();
            for (DayOfTheWeek day : days) {
                RestaurantScheduleManager.addSchedule(r, day, hhmm(row.get("start1")), hhmm(row.get("end1")), Integer.parseInt(row.get("capacity1")));
                RestaurantScheduleManager.addSchedule(r, day, hhmm(row.get("start2")), hhmm(row.get("end2")), Integer.parseInt(row.get("capacity2")));
            }

            repo.addRestaurant(r);
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
            LocalTime t = hhmm(s);
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

    @When("je filtre les restaurants avec un prix max de {double} euros")
    public void je_filtre_par_prix_max(double p) {
        resultats = service.filterByPriceMax(p);
    }

    @When("je filtre les restaurants avec un prix entre {double} et {double} euros")
    public void je_filtre_par_prix_min_et_max(double p1, double p2) {
        resultats = service.filterByPriceRange(p1,p2);
    }

    @When("je filtre les restaurants avec un prix min de {double} euros")
    public void je_filtre_par_prix_min(double p) {
        resultats = service.filterByPriceMin(p);
    }

    @When("je filtre les restaurants par nom {string}")
    public void je_filtre_par_nom(String name) {
        resultats = service.findByName(name);
    }

    // 1) Un seul tag (enum OU label)
    @When("je filtre les restaurants par tag {string}")
    public void je_filtre_les_restaurants_par_tag(String tagText) {
        DishTag tag = toTag(tagText);           // convertit "VEGAN" ou "Vegan" ou "Gluten-free"
        resultats = service.filterByTag(tag);   // -> List<Restaurant>
    }

    // 2) Tous les tags (séparés par , ou ;)
    @When("je filtre les restaurants par tous les tags {string}")
    public void je_filtre_par_tous_les_tags(String tagsText) {
        List<DishTag> tags = parseTags(tagsText);   // ex: "VEGETARIAN, LACTOSE_FREE" ou "Vegan; Organic"
        resultats = service.filterByAllTags(tags);
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
                .map(RestaurantFilterSteps::toTag)
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