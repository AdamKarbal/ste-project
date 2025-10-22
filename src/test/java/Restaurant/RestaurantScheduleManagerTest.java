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
    void testAddScheduleOverlapOrUpdate() {
        Restaurant r = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(new ArrayList<>())
                .build();

        RestaurantScheduleManager.addSchedule(r, DayOfTheWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0), 20);

        RestaurantScheduleManager.addSchedule(r, DayOfTheWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(13, 0), 5);

        LocalTime t = LocalTime.of(11, 0);
        while (t.isBefore(LocalTime.of(12, 0))) {
            LocalTime finalT = t;
            long count = r.getRestaurantSchedules().stream()
                    .filter(s -> s.getDay() == DayOfTheWeek.MONDAY && s.getStart().equals(finalT))
                    .count();
            assertEquals(1, count, "Il ne doit y avoir qu'un seul schedule pour " + t);
            t = t.plusMinutes(30);
        }

        t = LocalTime.of(11, 0);
        while (t.isBefore(LocalTime.of(12, 0))) {
            LocalTime finalT = t;
            int capacity = r.getRestaurantSchedules().stream()
                    .filter(s -> s.getDay() == DayOfTheWeek.MONDAY && s.getStart().equals(finalT))
                    .findFirst().get()
                    .getCapacity();
            assertEquals(20, capacity, "La capacité pour " + t + " ne doit pas être mise à jour");
            t = t.plusMinutes(30);
        }

        t = LocalTime.of(12, 0);
        while (t.isBefore(LocalTime.of(13, 0))) {
            LocalTime finalT = t;
            boolean exists = r.getRestaurantSchedules().stream()
                    .anyMatch(s -> s.getDay() == DayOfTheWeek.MONDAY && s.getStart().equals(finalT));
            assertTrue(exists, "Un schedule doit exister pour " + t);
            t = t.plusMinutes(30);
        }
    }

    @Test
    void testRemoveScheduleById_Local() {
        Restaurant restaurant = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(new ArrayList<>())
                .build();

        RestaurantSchedule mondayMorning = new RestaurantSchedule(DayOfTheWeek.MONDAY, LocalTime.of(8, 0), 10);
        restaurant.addSchedule(mondayMorning);

        boolean removed = RestaurantScheduleManager.removeScheduleById(restaurant, mondayMorning.getId());

        assertTrue(removed, "Le schedule doit être supprimé");
        assertFalse(restaurant.getRestaurantSchedules().contains(mondayMorning),
                "Le schedule ne doit plus exister dans la liste");
    }

    @Test
    void testUpdateCapacityById_Local() {
        Restaurant restaurant = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(new ArrayList<>())
                .build();

        RestaurantSchedule mondayEvening = new RestaurantSchedule(DayOfTheWeek.MONDAY, LocalTime.of(18, 0), 20);
        restaurant.addSchedule(mondayEvening);

        boolean updated = RestaurantScheduleManager.updateCapacityById(restaurant, mondayEvening.getId(), 99);

        assertTrue(updated, "La mise à jour doit réussir");
        assertEquals(99, mondayEvening.getCapacity(), "La capacité doit être mise à jour à 99");
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
