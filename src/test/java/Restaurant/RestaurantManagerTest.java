package Restaurant;

import Api.DishRepo.DishRepoServer;
import Api.RestaurantRepo.RestaurantRepoServer;
import Api.RestaurantSchedule.RestaurantScheduleServer;
import Api.Utils.Requester;
import Dish.*;
import Dish.suggest.Suggestion;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.establishmentType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import io.cucumber.java.Before;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantManagerTest {

    private static HttpServer serverRepo;
    private static HttpServer serverSchedule;
    private static HttpServer dishRepo;
    private static boolean isServerRunning = false;
    private RestaurantManager manager;
    private final String TEST_RESTO_NAME = "Chez akhi ali";

    @BeforeAll
    public static void setup() throws IOException {
        if (!isServerRunning) {
            try {
                serverRepo = RestaurantRepoServer.startServer();
                serverSchedule = RestaurantScheduleServer.startServer();
                dishRepo = DishRepoServer.startServer();
                isServerRunning = true;
            } catch (java.net.BindException e) {
                System.out.println(" Serveur déjà lancé ");
                isServerRunning = true;
            }
        }
    }

    @AfterAll
    static void stopInfrastructure() {
        if (serverRepo != null) {
            serverRepo.stop(0);
        }
        if (serverSchedule != null) {
            serverSchedule.stop(0);
        }
        if(dishRepo!=null){
            dishRepo.stop(0);
        }
    }

    @BeforeEach
    void setUp(){
        manager = new RestaurantManager();
        Restaurant initialRestaurant = new Restaurant.Builder()
                .name(TEST_RESTO_NAME)
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .cuisineType(new ArrayList<>())
                .establishmentType(null)
                .build();
        manager.saveRestaurant(initialRestaurant);
    }

    @Test
    void testUpdateCapacity_ChangesExistingCapacity() throws Exception {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);
        DayOfTheWeek day = DayOfTheWeek.WEDNESDAY;
        Requester.addSchedule(TEST_RESTO_NAME, day, start, end, 20);
        List<RestaurantSchedule> schedules = Requester.getSchedules(TEST_RESTO_NAME);
        String id = schedules.get(0).getId();
        Requester.updateScheduleCapacity(TEST_RESTO_NAME, id, 50);
        Restaurant updatedResto = Requester.getRestaurantByName(TEST_RESTO_NAME);
        assertEquals(50, updatedResto.getRestaurantSchedules().get(0).getCapacity());
    }

    @Test
    void testUpdateDish_ReplacesOldDishWithNewOne() throws Exception {
        Dish oldDish = new Dish(10f, "Old Dish", "", DishCategory.STARTER, DishType.MEAT, new ArrayList<>());
        Dish newDish = new Dish(12f, "New Dish", "Nouvelle description", DishCategory.STARTER, DishType.MEAT, new ArrayList<>());

        Dish oldDishAdded = manager.addDish(oldDish, TEST_RESTO_NAME);
        manager.updateDish(oldDishAdded, newDish, TEST_RESTO_NAME);

        Restaurant updatedResto = Requester.getRestaurantByName(TEST_RESTO_NAME);

        boolean containsOld = updatedResto.getDishes().stream().anyMatch(d -> d.getName().equals("Old Dish"));
        boolean containsNew = updatedResto.getDishes().stream().anyMatch(d -> d.getName().equals("New Dish"));

        assertFalse(containsOld);
        assertTrue(containsNew);
    }

    @Test
    void testAddCuisineType_AddsNewCuisine() throws Exception {
        manager.addCuisineType(CuisineType.ITALIENNE, TEST_RESTO_NAME);

        Restaurant updatedResto = Requester.getRestaurantByName(TEST_RESTO_NAME);
        assertTrue(updatedResto.getCuisineTypes().contains(CuisineType.ITALIENNE));
    }

    @Test
    void testRemoveCuisineType_RemovesExistingCuisine() throws Exception {
        manager.addCuisineType(CuisineType.FRANCAISE, TEST_RESTO_NAME);

        manager.removeCuisineType(CuisineType.FRANCAISE, TEST_RESTO_NAME);

        Restaurant updatedResto = Requester.getRestaurantByName(TEST_RESTO_NAME);
        assertFalse(updatedResto.getCuisineTypes().contains(CuisineType.FRANCAISE));
    }

    @Test
    void previewSuggestion_returnsExpectedSuggestion() {
        Suggestion s = manager.previewSuggestion("Pizza Margherita");
        assertNotNull(s);
        assertEquals(DishType.PIZZA, s.type);
        assertNotNull(s.suggestedPrice);
    }
}