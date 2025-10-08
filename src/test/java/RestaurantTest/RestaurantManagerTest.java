package RestaurantTest;

import Dish.Dish;
import Restaurant.*;
import Utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantManagerTest {

    private RestaurantManager manager;
    private Restaurant restaurant;

    private static Date d(int day, int month, int year, int hour, int minute) throws Exception {
        return new Date.Builder()
                .day(day).month(month).year(year)
                .hour(hour).minute(minute)
                .build();
    }

    @BeforeEach
    void setUp() throws Exception {
        RestaurantRepo repo = new RestaurantRepo(new ArrayList<>());
        new RestaurantService(repo);
        manager = new RestaurantManager("Chez akhi ali");
        restaurant = manager.getRestaurant();
    }

    @Test
    void testUpdateSchedule_AddNewScheduleIfNotExists() throws Exception {
        Date start = d(5, 10, 2025, 12, 0);
        Date end   = d(5, 10, 2025, 15, 0);
        manager.updateSchedule(start, end, 40);
        assertEquals(1, restaurant.getRestaurantSchedules().size());
        assertEquals(40, restaurant.getRestaurantSchedules().get(0).getCapacity());
    }

    @Test
    void testUpdateCapacity_ChangesExistingCapacity() throws Exception {
        Date start = d(5, 10, 2025, 10, 0);
        Date end   = d(5, 10, 2025, 14, 0);
        RestaurantSchedule rs = new RestaurantSchedule(start, end, 20);
        restaurant.setRestaurantSchedules(new ArrayList<>(List.of(rs)));
        manager.updateCapacity(start, end, 50);
        assertEquals(50, restaurant.getRestaurantSchedules().get(0).getCapacity());
    }

    @Test
    void testUpdateDish_ReplacesOldDishWithNewOne() throws Exception {
        Dish oldDish = new Dish();
        oldDish.setPrice(10.0);
        Dish newDish = new Dish();
        newDish.setPrice(12.0);
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
