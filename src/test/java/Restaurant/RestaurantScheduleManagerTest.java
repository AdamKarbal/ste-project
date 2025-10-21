package Restaurant;

import Restaurant.Enum.DayOfTheWeek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantScheduleManagerTest {

    private Restaurant restaurant;
    private RestaurantSchedule mondayMorning;
    private RestaurantSchedule mondayEvening;
    private RestaurantSchedule fridayNight;

    @BeforeEach
    void setUp() {
        mondayMorning = new RestaurantSchedule(DayOfTheWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 10);
        mondayEvening = new RestaurantSchedule(DayOfTheWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(23, 0), 20);
        fridayNight = new RestaurantSchedule(DayOfTheWeek.FRIDAY, LocalTime.of(22, 0), LocalTime.of(2, 0), 15); // Overnight

        restaurant = new Restaurant.Builder()
                .name("Testaurant")
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>(List.of(mondayMorning, mondayEvening, fridayNight)))
                .cuisineType(new ArrayList<>())
                .build();
    }

    @Test
    void testAddScheduleSuccess() {
        RestaurantSchedule newSchedule = new RestaurantSchedule(DayOfTheWeek.TUESDAY, LocalTime.of(12, 0), LocalTime.of(14, 0), 10);
        boolean added = RestaurantScheduleManager.addSchedule(restaurant, newSchedule);
        assertTrue(added);
        assertTrue(restaurant.getRestaurantSchedules().contains(newSchedule));
    }

    @Test
    void testAddScheduleOverlapFails() {
        RestaurantSchedule overlap = new RestaurantSchedule(DayOfTheWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(13, 0), 5);
        boolean added = RestaurantScheduleManager.addSchedule(restaurant, overlap);
        assertFalse(added);
    }

    @Test
    void testRemoveScheduleById() {
        String id = mondayMorning.getId();
        boolean removed = RestaurantScheduleManager.removeScheduleById(restaurant, id);
        assertTrue(removed);
        assertFalse(restaurant.getRestaurantSchedules().contains(mondayMorning));
    }

    @Test
    void testUpdateCapacityById() {
        String id = mondayEvening.getId();
        boolean updated = RestaurantScheduleManager.updateCapacityById(restaurant, id, 99);
        assertTrue(updated);
        assertEquals(99, mondayEvening.getCapacity());
    }

    @Test
    void testGetTodaySchedules() {
        var now = java.time.LocalTime.now();
        var today = RestaurantScheduleManager.convertDayOfWeek(java.time.LocalDate.now().getDayOfWeek());

        var currentSchedule = new RestaurantSchedule(
                today,
                now.minusMinutes(30),
                now.plusMinutes(30),
                10
        );

        restaurant.addSchedule(currentSchedule);

        var schedules = RestaurantScheduleManager.getTodaySchedules(restaurant);

        assertNotNull(schedules);
        assertTrue(
                schedules.contains(currentSchedule),
                "Le créneau actif du jour devrait être retourné"
        );
    }

    @Test
    void testIsOpenAt() {
        boolean open = RestaurantScheduleManager.isOpenAt(
                restaurant,
                java.time.LocalDateTime.of(2025, 10, 13, 10, 0) // Monday
        );
        assertTrue(open);
    }

    @Test
    void testIsClosedAt() {
        boolean open = RestaurantScheduleManager.isOpenAt(
                restaurant,
                java.time.LocalDateTime.of(2025, 10, 13, 3, 0)
        );
        assertFalse(open);
    }

    @Test
    void testOvernightScheduleOpenPastMidnight() {
        boolean openFridayNight = RestaurantScheduleManager.isOpenAt(
                restaurant,
                java.time.LocalDateTime.of(2025, 10, 17, 23, 30)
        );
        assertTrue(openFridayNight);

        boolean openSaturdayEarly = RestaurantScheduleManager.isOpenAt(
                restaurant,
                java.time.LocalDateTime.of(2025, 10, 18, 1, 0)
        );
        assertTrue(openSaturdayEarly);
    }

    @Test
    void testOvernightScheduleClosedAfterEnd() {
        boolean open = RestaurantScheduleManager.isOpenAt(
                restaurant,
                java.time.LocalDateTime.of(2025, 10, 18, 3, 0)
        );
        assertFalse(open);
    }
}
