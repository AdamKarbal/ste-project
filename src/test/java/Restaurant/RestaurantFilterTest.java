package Restaurant;

import Api.RestaurantRepo.RestaurantRepoServer;
import Api.RestaurantSchedule.RestaurantScheduleServer;
import Api.Utils.Requester;
import Dish.Dish;
import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.establishmentType;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantFilterTest {

    private static RestaurantService service;

    private static HttpServer serverSchedule;
    private static HttpServer serverRestoRepo;
    private static List<HttpServer> servers;

    @BeforeAll
    static void setup() {
        try {
            serverSchedule = RestaurantScheduleServer.startServer();
            serverRestoRepo = RestaurantRepoServer.startServer();
            servers = List.of(serverSchedule, serverRestoRepo);
            setupRestos();
        } catch (Exception e) {
            System.out.println("Servers could not be started: " + e.getMessage());
        }
    }

    @AfterAll
    public static void stopServers() {
        if (servers != null) {
            for (HttpServer server : servers) {
                if (server != null) {
                    server.stop(0);
                }
            }
        }
    }


    public static void setupRestos() throws Exception {
        Requester.clearRestaurants();

        // --- Création des Dishs avec tags/prix ---
        Dish dishSansGlutenEtVegan = new Dish(0f, "Dish SG+V", null, null, null,
                new ArrayList<>(List.of(DishTag.GLUTEN_FREE, DishTag.VEGAN)));

        Dish dishVegan = new Dish(0f, "Dish Vegan", null, null, null,
                new ArrayList<>(List.of(DishTag.VEGAN)));

        Dish dishSansGluten = new Dish(0f, "Dish SG", null, null, null,
                new ArrayList<>(List.of(DishTag.GLUTEN_FREE)));

        Dish dishEpice = new Dish(0f, "Dish Spicy", null, null, null,
                new ArrayList<>(List.of(DishTag.SPICY)));

        Dish cheapDish = new Dish(10f, "Dish Cheap", null, null, null,
                new ArrayList<>());

        Dish mediumDish = new Dish(20f, "Dish Medium", null, null, null,
                new ArrayList<>());

        Dish expensiveDish = new Dish(30f, "Dish Expensive", null, null, null,
                new ArrayList<>());

        // --- Création des Restaurants côté Java (pour construire le payload) ---
        Restaurant restoA = new Restaurant.Builder()
                .name("Pizza Palace")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishSansGluten, dishVegan, dishSansGlutenEtVegan))
                .cuisineType(List.of(CuisineType.ITALIENNE))
                .build();

        Restaurant restoB = new Restaurant.Builder()
                .name("La pizza roma")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishVegan))
                .cuisineType(List.of(CuisineType.ITALIENNE))
                .build();

        Restaurant restoC = new Restaurant.Builder()
                .name("Burger King")
                .establishmentType(establishmentType.FAST_FOOD)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishEpice))
                .cuisineType(List.of(CuisineType.AMERICAINE))
                .build();

        Restaurant restoD = new Restaurant.Builder()
                .name("Pasta House")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishSansGluten))
                .cuisineType(List.of(CuisineType.FRANCAISE))
                .build();

        // Ajuste les plats (prix) avant envoi
        restoA.setDishes(List.of(
                cheapDish, mediumDish, dishSansGluten, dishVegan, dishSansGlutenEtVegan
        ));
        restoB.setDishes(List.of(expensiveDish, dishVegan));
        restoC.setDishes(List.of(mediumDish, dishEpice));
        restoD.setDishes(List.of(dishSansGluten));

        // --- Envoi des restos vers l’API via Requester ---
        Requester.createRestaurant(restoA);
        Requester.createRestaurant(restoB);
        Requester.createRestaurant(restoC);
        Requester.createRestaurant(restoD);

        // --- Création des horaires pour les tests d’ouverture ---
        for (DayOfTheWeek day : DayOfTheWeek.getRealDays()) {
            // Pizza Palace ouvert 00:00–23:59
            Requester.addSchedule(restoA.getName(), day,
                    LocalTime.of(0, 0), LocalTime.of(23, 59), 100);
        }
        // Les autres n’ont pas de schedule → considérés fermés par défaut

        // --- Service qui utilisera RestaurantFilter en internes ---
        service = new RestaurantService();
    }

    /* ---------------------- Helpers ---------------------- */

    private static List<String> namesOf(List<Restaurant> restos) {
        return restos.stream().map(Restaurant::getName).toList();
    }

    /* ---------------------- Tests ---------------------- */

    // Filter by name

    @Test
    void findName_partialMatchPizza() {
        List<Restaurant> result = service.findByName("pizza");
        List<String> names = namesOf(result);

        assertEquals(2, result.size());
        assertTrue(names.contains("Pizza Palace"));
        assertTrue(names.contains("La pizza roma"));
    }

    @Test
    void findName_caseInsensitive() {
        List<Restaurant> result = service.findByName("PIZZA");
        assertEquals(2, result.size());
    }

    @Test
    void findName_noMatch() {
        List<Restaurant> result = service.findByName("sushi");
        assertTrue(result.isEmpty());
    }

    // Filter by type

    @Test
    void filterByType_returnAllRestaurants() {
        List<Restaurant> result = service.filterByType(establishmentType.RESTAURANT);
        List<String> names = namesOf(result);

        assertEquals(3, result.size());
        assertTrue(names.contains("Pizza Palace"));
        assertTrue(names.contains("La pizza roma"));
        assertTrue(names.contains("Pasta House"));
    }

    @Test
    void filterByType_noMatch() {
        List<Restaurant> result = service.filterByType(establishmentType.SUSHI_BAR);
        assertTrue(result.isEmpty());
    }

    // Filter by availability

    @Test
    void filterByAvailability_shouldReturnOnlyAlwaysOpenRestaurant() {
        List<Restaurant> result = service.filterByAvailability();
        List<String> names = namesOf(result);

        assertEquals(1, result.size());
        assertTrue(names.contains("Pizza Palace"));
    }

    @Test
    void filterByAvailability_atSpecificTime() {
        LocalTime target = LocalTime.of(12, 0);
        List<Restaurant> result = service.filterByAvailability(target);
        List<String> names = namesOf(result);

        assertEquals(1, result.size());
        assertTrue(names.contains("Pizza Palace"));
    }

    // Filter by CuisineType

    @Test
    void filterByCuisineType_shouldReturnMatchingRestaurants() {
        // On part de l’état serveur, donc on reconfigure via l’API si besoin
        // (optionnel ici car restoA et restoB sont déjà italiens, restoC américain)

        List<Restaurant> result = service.filterByCuisine(CuisineType.ITALIENNE);
        List<String> names = namesOf(result);

        assertEquals(2, result.size());
        assertTrue(names.contains("Pizza Palace"));
        assertTrue(names.contains("La pizza roma"));
    }

    @Test
    void filterByCuisineType_shouldReturnEmptyIfNoMatch() {
        List<Restaurant> result = service.filterByCuisine(CuisineType.MEXICAINE);
        assertTrue(result.isEmpty());
    }

    // Filter by tag

    @Test
    void filterByTag_shouldReturnRestaurantsWithGivenTag() {
        List<Restaurant> result = service.filterByTag(DishTag.GLUTEN_FREE);
        List<String> names = namesOf(result);

        // Pizza Palace (plat sans gluten) + Pasta House (Dish SG)
        assertEquals(2, result.size());
        assertTrue(names.contains("Pizza Palace"));
        assertTrue(names.contains("Pasta House"));
    }

    @Test
    void filterByTag_shouldReturnEmptyIfNoRestaurantHasTag() {
        List<Restaurant> result = service.filterByTag(DishTag.HALAL);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterByAllTags_shouldReturnRestaurantsWithAllTags() {
        List<Restaurant> result = service.filterByAllTags(
                List.of(DishTag.GLUTEN_FREE, DishTag.VEGAN)
        );
        List<String> names = namesOf(result);

        assertEquals(1, result.size());
        assertTrue(names.contains("Pizza Palace"));
    }

    // Filter by price

    @Test
    void filterByPriceMin_shouldReturnRestaurantsWithDishAboveMinPrice() {
        List<Restaurant> result = service.filterByPriceMin(25.0);
        List<String> names = namesOf(result);

        assertEquals(1, result.size());
        assertTrue(names.contains("La pizza roma")); // a un plat à 30
    }

    @Test
    void filterByPriceMax_shouldReturnRestaurantsWithDishBelowMaxPrice() {
        List<Restaurant> result = service.filterByPriceMax(15.0);
        List<String> names = namesOf(result);

        // Pizza Palace (10) et Pasta House (0) sont sous 15
        assertEquals(2, result.size());
        assertTrue(names.contains("Pizza Palace"));
        assertTrue(names.contains("Pasta House"));
    }

    @Test
    void filterByPriceRange_shouldReturnRestaurantsWithDishInRange() {
        List<Restaurant> result = service.filterByPriceRange(10.0, 20.0);
        List<String> names = namesOf(result);

        assertEquals(2, result.size());
        assertTrue(names.contains("Pizza Palace")); // 10 & 20
        assertTrue(names.contains("Burger King"));  // 20
    }

    // Filter with combined criteria (FilterCriteria via service)

    @Test
    void filter_combinesMultipleCriteriaCorrectly() {
        FilterCriteria criteria = FilterCriteria.builder()
                .cuisine(CuisineType.ITALIENNE)
                .priceMax(15.0)
                .availableAt(LocalTime.of(12, 0))
                .tagsAnyOf(List.of(DishTag.VEGAN))
                .build();

        List<Restaurant> result = service.filter(criteria);
        List<String> names = namesOf(result);

        assertEquals(1, result.size(), "Un seul restaurant doit correspondre");
        assertTrue(names.contains("Pizza Palace"));
    }

    @Test
    void filter_returnsAllIfNoCriteria() {
        FilterCriteria criteria = FilterCriteria.builder().build();

        List<Restaurant> result = service.filter(criteria);
        List<String> names = namesOf(result);

        // On attend nos 4 restos créés dans setUp
        assertTrue(names.containsAll(
                List.of("Pizza Palace", "La pizza roma", "Burger King", "Pasta House")
        ));
        assertEquals(4, result.size());
    }

    @Test
    void filter_filtersByNameAndCuisine() {
        FilterCriteria criteria = FilterCriteria.builder()
                .nameContains("pasta")
                .cuisine(CuisineType.FRANCAISE)
                .build();

        List<Restaurant> result = service.filter(criteria);
        List<String> names = namesOf(result);

        assertEquals(1, result.size());
        assertTrue(names.contains("Pasta House"));
    }

    @Test
    void filter_filtersByAllTags() {
        FilterCriteria criteria = FilterCriteria.builder()
                .tagsAllOf(List.of(DishTag.VEGAN, DishTag.GLUTEN_FREE))
                .build();

        List<Restaurant> result = service.filter(criteria);
        List<String> names = namesOf(result);

        assertEquals(1, result.size());
        assertTrue(names.contains("Pizza Palace"));
    }
}
