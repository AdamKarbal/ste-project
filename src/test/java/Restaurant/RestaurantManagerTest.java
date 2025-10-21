package Restaurant;

import Dish.*;
import Dish.suggest.Suggestion;
import Restaurant.*;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.cuisineType;
import Restaurant.Exception.DishInDishes;
import Utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantManagerTest {

    private RestaurantManager manager;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() throws Exception {
        RestaurantRepo repo = new RestaurantRepo(new ArrayList<>());
        new RestaurantService(repo);
        manager = new RestaurantManager("Chez akhi ali");
        restaurant = manager.getRestaurant();
    }

    @Test
    void testUpdateSchedule_AddNewScheduleIfNotExists() {
        LocalTime start = LocalTime.of(12, 0);
        LocalTime end = LocalTime.of(15, 0);

        manager.updateSchedule(DayOfTheWeek.MONDAY, start, end, 40);

        assertEquals(1, restaurant.getRestaurantSchedules().size());
        assertEquals(40, restaurant.getRestaurantSchedules().get(0).getCapacity());
        assertEquals(DayOfTheWeek.MONDAY, restaurant.getRestaurantSchedules().get(0).getDay());
    }

    @Test
    void testUpdateCapacity_ChangesExistingCapacity() throws Exception {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);
        DayOfTheWeek day = DayOfTheWeek.WEDNESDAY;

        RestaurantScheduleManager.addSchedule(restaurant, day, start, end, 20);

        RestaurantScheduleManager.updateCapacity(restaurant, day, start, end, 50);

        assertEquals(50, restaurant.getRestaurantSchedules().get(0).getCapacity());
    }

    @Test
    void testUpdateDish_ReplacesOldDishWithNewOne() throws Exception {
        Dish oldDish = new Dish(10f, "Old Dish", null, null, null, new ArrayList<>());

        Dish newDish = new Dish(12f, "New Dish", null, null, null, new ArrayList<>());

        restaurant.setDishes(new ArrayList<>(List.of(oldDish)));
        manager.updateDish(oldDish, newDish);
        assertFalse(restaurant.getDishes().contains(oldDish));
        assertTrue(restaurant.getDishes().contains(newDish));
    }

    @Test
    void testAddCuisineType_AddsNewCuisine() throws Exception {
        manager.addCuisineType(cuisineType.ITALIENNE);
        assertTrue(restaurant.getCuisineTypes().contains(cuisineType.ITALIENNE));
    }

    @Test
    void testRemoveCuisineType_RemovesExistingCuisine() throws Exception {
        restaurant.setCuisineTypes(new ArrayList<>(List.of(cuisineType.FRANCAISE)));
        manager.removeCuisineType(cuisineType.FRANCAISE);
        assertFalse(restaurant.getCuisineTypes().contains(cuisineType.FRANCAISE));
    }

    @Test
    void previewSuggestion_returnsExpectedSuggestion() {
        RestaurantManager manager = new RestaurantManager("Chez Test");

        Suggestion s = manager.previewSuggestion("Pizza Margherita");

        assertNotNull(s);
        assertEquals(DishType.PIZZA, s.type);   // attendu via KeywordSuggester
        assertNotNull(s.suggestedPrice);
        assertTrue(s.toppings == null || !s.toppings.isEmpty());
    }

    @Test
    void createDish_mergesUserAndSuggestion_andAddsToRestaurant() throws Exception {
        RestaurantManager manager = new RestaurantManager("Chez Test");

        // Saisie utilisateur (il ajuste le prix et ajoute un topping)
        DishInput in = new DishInput();
        in.name = "Pizza Margherita";                   // déclenche PIZZA
        in.price = 12.0;                                // l’utilisateur prime > suggestion (10.0)
        in.toppings = List.of(new DishTopping("Olives", 0.8f)); // ajout manuel

        Dish created = manager.createDish(in);

        // Vérifs
        assertEquals(1, manager.getRestaurant().getDishes().size());
        assertEquals("Pizza Margherita", created.getName());
        assertEquals(12.0, created.getPrice(), 1e-6);     // user > suggestion
        assertTrue(created.getAvailableToppings().stream()
                .anyMatch(t -> t.getName().equalsIgnoreCase("Olives"))); // merge toppings
    }

    @Test
    void createDish_sameNameTwice_throwsDishInDishes_ifEqualsImplemented() throws Exception {
        RestaurantManager manager = new RestaurantManager("Chez Test");

        DishInput in = new DishInput();
        in.name = "Pizza Margherita";
        in.toppings = List.of(new DishTopping("Olives", 0.8f));

        manager.createDish(in); // OK 1ère fois

        // 2e création avec le même nom → si Dish.equals/hashCode(name) est implémenté,
        // RestaurantManager.addDish doit jeter DishInDishes.
        assertThrows(DishInDishes.class, () -> manager.createDish(in));
    }
}
