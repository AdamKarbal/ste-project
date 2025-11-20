package Restaurant;

import Api.RestaurantRepo.RestaurantRepoServer;
import Dish.*;
import Dish.suggest.Suggestion;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.establishmentType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantManagerTest {

    private static HttpServer server;
    private static boolean serverStartedByThisClass = false;

    private RestaurantManager manager;
    private final String TEST_RESTO_NAME = "Chez akhi ali";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeAll
    static void startInfrastructure() throws IOException {
        try {
            server = RestaurantRepoServer.startServer();
            serverStartedByThisClass = true;
        } catch (java.net.BindException e) {
            System.out.println("Serveur déjà en cours ");
        }
    }

    @AfterAll
    static void stopInfrastructure() {
        if (server != null && serverStartedByThisClass) {
            server.stop(0);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        manager = new RestaurantManager();
        Restaurant initialRestaurant = new Restaurant.Builder()
                .name(TEST_RESTO_NAME)
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .cuisineType(new ArrayList<>())
                .establishmentType(null)
                .build();
        forceUpdateRestaurantOnServer(initialRestaurant);
    }

    @Test
    void testUpdateSchedule_AddNewScheduleIfNotExists() throws Exception {
        LocalTime start = LocalTime.of(12, 0);
        LocalTime end = LocalTime.of(15, 0);
        manager.updateSchedule(DayOfTheWeek.MONDAY, start, end, 40, TEST_RESTO_NAME);
        Restaurant updatedResto = fetchRestaurantState(TEST_RESTO_NAME);
        assertEquals(6, updatedResto.getRestaurantSchedules().size());
        assertEquals(40, updatedResto.getRestaurantSchedules().get(0).getCapacity());
    }

    @Test
    void testUpdateCapacity_ChangesExistingCapacity() throws Exception {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);
        DayOfTheWeek day = DayOfTheWeek.WEDNESDAY;
        manager.updateSchedule(day, start, end, 20, TEST_RESTO_NAME);
        manager.updateCapacity(day, start, end, 50, TEST_RESTO_NAME);

        Restaurant updatedResto = fetchRestaurantState(TEST_RESTO_NAME);
        assertEquals(50, updatedResto.getRestaurantSchedules().get(0).getCapacity());
    }

    @Test
    void testUpdateDish_ReplacesOldDishWithNewOne() throws Exception {
        Dish oldDish = new Dish(10f, "Old Dish", null, null, null, new ArrayList<>());
        Dish newDish = new Dish(12f, "New Dish", null, null, null, new ArrayList<>());

        manager.addDish(oldDish, TEST_RESTO_NAME);
        manager.updateDish(oldDish, newDish, TEST_RESTO_NAME);

        Restaurant updatedResto = fetchRestaurantState(TEST_RESTO_NAME);

        boolean containsOld = updatedResto.getDishes().stream().anyMatch(d -> d.getName().equals("Old Dish"));
        boolean containsNew = updatedResto.getDishes().stream().anyMatch(d -> d.getName().equals("New Dish"));

        assertFalse(containsOld);
        assertTrue(containsNew);
    }

    @Test
    void testAddCuisineType_AddsNewCuisine() throws Exception {
        manager.addCuisineType(CuisineType.ITALIENNE, TEST_RESTO_NAME);

        Restaurant updatedResto = fetchRestaurantState(TEST_RESTO_NAME);
        assertTrue(updatedResto.getCuisineTypes().contains(CuisineType.ITALIENNE));
    }

    @Test
    void testRemoveCuisineType_RemovesExistingCuisine() throws Exception {
        manager.addCuisineType(CuisineType.FRANCAISE, TEST_RESTO_NAME);

        manager.removeCuisineType(CuisineType.FRANCAISE, TEST_RESTO_NAME);

        Restaurant updatedResto = fetchRestaurantState(TEST_RESTO_NAME);
        assertFalse(updatedResto.getCuisineTypes().contains(CuisineType.FRANCAISE));
    }

    @Test
    void previewSuggestion_returnsExpectedSuggestion() {
        Suggestion s = manager.previewSuggestion("Pizza Margherita");
        assertNotNull(s);
        assertEquals(DishType.PIZZA, s.type);
        assertNotNull(s.suggestedPrice);
    }


    private void forceUpdateRestaurantOnServer(Restaurant r) throws Exception {
        String json = mapper.writeValueAsString(r);
        String encodedName = URLEncoder.encode(r.getName(), StandardCharsets.UTF_8).replace("+", "%20");
        String url = "http://localhost:8002/api/restaurantrepo/" + encodedName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Restaurant fetchRestaurantState(String name) throws Exception {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
        String url = "http://localhost:8002/api/restaurantrepo/" + encodedName;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Failed to fetch state");

        Map<String, Object> attributes = mapper.readValue(response.body(), Map.class);

        ArrayList<Dish> dishes = mapper.convertValue(attributes.get("dishes"), new TypeReference<ArrayList<Dish>>() {});
        if (dishes == null) dishes = new ArrayList<>();

        ArrayList<RestaurantSchedule> schedules = mapper.convertValue(attributes.get("restaurantSchedules"), new TypeReference<ArrayList<RestaurantSchedule>>() {});
        if (schedules == null) schedules = new ArrayList<>();

        ArrayList<String> cuisineStrings = mapper.convertValue(attributes.get("cuisineTypes"), new TypeReference<ArrayList<String>>() {});
        ArrayList<CuisineType> cuisineTypes = new ArrayList<>();
        if (cuisineStrings != null) { for (String s : cuisineStrings) cuisineTypes.add(CuisineType.valueOf(s)); }

        establishmentType type = null;
        if(attributes.get("establishmentType") != null) { type = establishmentType.valueOf((String) attributes.get("establishmentType")); }

        return new Restaurant.Builder()
                .name((String) attributes.get("name"))
                .dishes(dishes)
                .restaurantSchedules(schedules)
                .cuisineType(cuisineTypes)
                .establishmentType(type)
                .build();
    }
}