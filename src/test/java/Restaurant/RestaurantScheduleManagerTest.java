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
        restaurant = new Restaurant.Builder()
                .name("Testaurant")
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .cuisineType(new ArrayList<>())
                .build();

        RestaurantScheduleManager.addSchedule(restaurant, DayOfTheWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 10);
        RestaurantScheduleManager.addSchedule(restaurant, DayOfTheWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(23, 0), 20);
        RestaurantScheduleManager.addSchedule(restaurant, DayOfTheWeek.FRIDAY, LocalTime.of(22, 0), LocalTime.of(2, 0), 15); // overnight
    }

    @Test
    void testAddScheduleSuccess() {
        Restaurant r = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(new ArrayList<>())
                .build();

        boolean added = RestaurantScheduleManager.addSchedule(r, DayOfTheWeek.TUESDAY, LocalTime.of(12, 0), LocalTime.of(14, 0), 10);

        assertTrue(added, "La plage devrait être ajoutée avec succès");

        LocalTime current = LocalTime.of(12, 0);
        while (!current.equals(LocalTime.of(14, 0))) {
            final LocalTime slotStart = current;
            assertTrue(r.getRestaurantSchedules().stream()
                            .anyMatch(s -> s.getDay() == DayOfTheWeek.TUESDAY && s.getStart().equals(slotStart)),
                    "Le créneau " + slotStart + " - " + slotStart.plusMinutes(30) + " devrait exister");
            current = current.plusMinutes(30);
        }
    }

    @Test
    void testAddScheduleOverlapFails() {
        Restaurant r = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(new ArrayList<>())
                .build();
        RestaurantScheduleManager.addSchedule(r, DayOfTheWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0), 20);

        boolean added = RestaurantScheduleManager.addSchedule(r, DayOfTheWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(13, 0), 5);

        assertFalse(added, "La plage qui chevauche ne devrait pas être ajoutée");
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
        Restaurant r = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(new ArrayList<>())
                .build();

        var now = java.time.LocalTime.now();
        var today = RestaurantScheduleManager.convertDayOfWeek(java.time.LocalDate.now().getDayOfWeek());

        LocalTime start = now.minusMinutes(30).withMinute(now.getMinute() < 30 ? 0 : 30); // arrondi au créneau précédent
        LocalTime end = start.plusMinutes(60); // couvre deux créneaux de 30 min

        RestaurantScheduleManager.addSchedule(r, today, start, end, 10);

        var schedules = RestaurantScheduleManager.getTodaySchedules(r);

        assertNotNull(schedules);

        boolean found = schedules.stream()
                .anyMatch(s -> !now.isBefore(s.getStart()) && now.isBefore(s.getEnd()));

        assertTrue(found, "Le créneau actif du jour devrait être retourné");
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
    void testOvernightScheduleClosedAfterEnd() {
        boolean open = RestaurantScheduleManager.isOpenAt(
                restaurant,
                java.time.LocalDateTime.of(2025, 10, 18, 3, 0)
        );
        assertFalse(open);
    }
}
