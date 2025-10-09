package steps;

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
import Restaurant.Enum.cuisineType;
import Restaurant.Enum.establishmentType;

public class RestaurantFilterSteps {

    private RestaurantRepo repo;
    private RestaurantService service;
    private List<Restaurant> resultats = new ArrayList<>();

    // Ré-initialise un repo propre pour CHAQUE scénario
    @Before
    public void setup() {
        repo = new RestaurantRepo(new ArrayList<>());
        service = new RestaurantService(repo); // assigne le repo statique et crée le filtre
    }

    @Given("un catalogue de restaurants")
    public void un_catalogue_de_restaurants(DataTable table) {
        // Table: | nom | cuisines | type |
        for (Map<String, String> row : table.asMaps()) {
            String nom = row.get("nom").trim();
            String cuisinesCell = row.getOrDefault("cuisines", "").trim();
            String typeCell = row.getOrDefault("type", "RESTAURANT").trim();

            List<cuisineType> cuisines = parseCuisines(cuisinesCell);
            establishmentType etabType = parseEstablishmentType(typeCell);

            Restaurant r = new Restaurant.Builder()
                    .name(nom)
                    .dishes(new ArrayList<>())
                    .restaurantSchedules(new ArrayList<>())
                    .cuisineType(cuisines)
                    .establishmentType(etabType)
                    .build();

            try {
                RestaurantService.addRestaurant(r); // passe par ton service et ton repo réel
            } catch (Exception e) {
                throw new RuntimeException("Ajout restaurant impossible: " + nom, e);
            }
        }
    }

    @When("je filtre les restaurants par cuisine {string}")
    public void je_filtre_les_restaurants_par_cuisine(String cuisine) {
        cuisineType type = resolveCuisineEnum(cuisine);
        resultats = service.filterByCuisine(type);
    }

    @Then("je vois exactement {int} restaurants")
    public void je_vois_exactement_n_restaurants(int attendu) {
        Assertions.assertEquals(attendu, resultats.size(),
                "Taille inattendue. Présents: " + names(resultats));
    }

    @Then("la liste contient {string}")
    public void la_liste_contient(String nom) {
        Assertions.assertTrue(resultats.stream().anyMatch(r -> r.getName().equals(nom)),
                "La liste ne contient pas: " + nom + " ; présents: " + names(resultats));
    }

    // ------- Helpers (normalisation accents/espaces/majuscules) -------

    private static List<cuisineType> parseCuisines(String cell) {
        if (cell.isBlank()) return new ArrayList<>();
        return Arrays.stream(cell.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(RestaurantFilterSteps::resolveCuisineEnum)
                .collect(Collectors.toList());
    }

    private static cuisineType resolveCuisineEnum(String s) {
        String norm = normalizeEnumToken(s);
        for (cuisineType ct : cuisineType.values()) {
            if (normalizeEnumToken(ct.name()).equals(norm)) return ct;
        }
        throw new IllegalArgumentException("Cuisine inconnue: " + s + " (attendues: " +
                Arrays.toString(cuisineType.values()) + ")");
    }

    private static establishmentType parseEstablishmentType(String s) {
        String norm = normalizeEnumToken(s.isBlank() ? "RESTAURANT" : s);
        for (establishmentType et : establishmentType.values()) {
            if (normalizeEnumToken(et.name()).equals(norm)) return et;
        }
        throw new IllegalArgumentException("Type d'établissement inconnu: " + s);
    }

    private static String normalizeEnumToken(String raw) {
        String up = raw.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        // supprime les accents (BRÉSILIENNE -> BRESILIENNE)
        return Normalizer.normalize(up, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
    }

    private static String names(List<Restaurant> rs) {
        return rs.stream().map(Restaurant::getName).collect(Collectors.joining(", "));
    }
}