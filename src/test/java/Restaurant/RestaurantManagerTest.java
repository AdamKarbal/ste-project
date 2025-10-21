package Restaurant;

import Dish.Dish;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.cuisineType;
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
}
