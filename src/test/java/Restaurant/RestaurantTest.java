package Restaurant;

import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.CuisineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private Restaurant restaurant;
    private RestaurantScheduleManager scheduleManager;

    private static Restaurant restaurantWithSingleSlot(DayOfTheWeek dayOfTheWeek, LocalTime start, LocalTime end, RestaurantScheduleManager scheduleManager) {
        Restaurant restaurant = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(new ArrayList<>())
                .build();
        scheduleManager.addSchedule("R", dayOfTheWeek, start, end, 10);
        return restaurant;
    }

    @BeforeEach
    void setUp() {
        scheduleManager = new RestaurantScheduleManager();
        restaurant = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(new ArrayList<>())
                .build();

        scheduleManager.addSchedule(restaurant.getName(), DayOfTheWeek.SUNDAY, LocalTime.of(10, 0), LocalTime.of(14, 0), 30);
        scheduleManager.addSchedule(restaurant.getName(), DayOfTheWeek.SUNDAY, LocalTime.of(18, 0), LocalTime.of(23, 0), 50);
        scheduleManager.addSchedule(restaurant.getName(), DayOfTheWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(2, 0), 20);
    }


    @Test
    void testIsWithin_NormalRangeInside() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(10, 0), LocalTime.of(14, 0), scheduleManager);

        LocalDateTime dt = LocalDateTime.of(2025, 10, 5, 12, 0); // 12h dans 10–14
        assertTrue(scheduleManager.isOpenAt(r.getName(), dt));
    }

    @Test
    void testIsWithin_NormalRangeOutside() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(10, 0), LocalTime.of(14, 0), scheduleManager);

        LocalDateTime dt = LocalDateTime.of(2025, 10, 5, 15, 0); // hors 10–14
        assertFalse(scheduleManager.isOpenAt(r.getName(), dt));
    }

    @Test
    void testIsWithin_OvernightInsideBeforeMidnight() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(2, 0), scheduleManager);

        LocalDateTime dt = LocalDateTime.of(2025, 10, 5, 23, 0); // 23h dans 22–02
        assertTrue(scheduleManager.isOpenAt(r.getName(), dt));
    }

    @Test
    void testIsWithin_OvernightInsideAfterMidnight() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(2, 0), scheduleManager);

        LocalDateTime dt = LocalDateTime.of(2025, 10, 6, 1, 0); // 1h du matin  toujours ouvert
        assertTrue(scheduleManager.isOpenAt(r.getName(), dt));
    }

    @Test
    void testIsWithin_OvernightOutside() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(2, 0), scheduleManager);

        LocalDateTime dt = LocalDateTime.of(2025, 10, 5, 18, 0); // 18h  avant ouverture
        assertFalse(scheduleManager.isOpenAt(r.getName(), dt));
    }

    @Test
    void testIsOpenAt_Open() {
        LocalDateTime current = LocalDateTime.of(2025, 10, 5, 19, 0); // dans 18–23
        assertTrue(scheduleManager.isOpenAt(restaurant.getName(), current));
    }

    @Test
    void testIsOpenAt_Closed() {
        LocalDateTime current = LocalDateTime.of(2025, 10, 5, 15, 0); // entre 14 et 18 fermé
        assertFalse(scheduleManager.isOpenAt(restaurant.getName(), current));
    }

    @Test
    void testIsOpenAt_OvernightOpenAfterMidnight() {
        LocalDateTime current = LocalDateTime.of(2025, 10, 6, 1, 0); // 1h  dans 22–02
        assertTrue(scheduleManager.isOpenAt(restaurant.getName(), current));
    }

    @Test
    void testAlreadyCuisineType_WhenTypeAlreadyPresent() {
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .cuisineType(List.of(CuisineType.ITALIENNE, CuisineType.JAPONAISE))
                .build();

        assertTrue(r.alreadyCuisineType(CuisineType.ITALIENNE));
    }

    @Test
    void testAlreadyCuisineType_WhenTypeNotPresent() {
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .cuisineType(List.of(CuisineType.ITALIENNE, CuisineType.JAPONAISE))
                .build();

        assertFalse(r.alreadyCuisineType(CuisineType.MEXICAINE));
    }

    @Test
    void testAlreadyScheduled_WhenScheduleExists() {
        DayOfTheWeek day = DayOfTheWeek.MONDAY;
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);

        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(new ArrayList<>())
                .build();

        scheduleManager.addSchedule(r.getName(), day, start, end, 20);

        LocalTime current = start;
        while (!current.equals(end)) {
            assertTrue(r.alreadyScheduled(current, current.plusMinutes(30), day),
                    "Le créneau " + current + " : " + current.plusMinutes(30) + " devrait exister");
            current = current.plusMinutes(30);
        }
    }

    @Test
    void testAlreadyScheduled_WhenNoMatchingSchedule() {
        DayOfTheWeek day = DayOfTheWeek.MONDAY;
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);

        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(new ArrayList<>())
                .build();

        scheduleManager.addSchedule(r.getName(), day, start, end, 20);

        LocalTime otherStart = LocalTime.of(15, 0);
        LocalTime otherEnd = LocalTime.of(18, 0);

        LocalTime current = otherStart;
        boolean anyExists = false;
        while (!current.equals(otherEnd)) {
            if (r.alreadyScheduled(current, current.plusMinutes(30), day)) {
                anyExists = true;
                break;
            }
            current = current.plusMinutes(30);
        }

        assertFalse(anyExists, "Aucun créneau de 15:00 - 18:00 ne devrait exister");
    }

    @Test
    void testAlreadyScheduled_WhenListEmpty() {
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of())
                .build();

        DayOfTheWeek day = DayOfTheWeek.MONDAY;
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);

        assertFalse(r.alreadyScheduled(start, end, day));
    }

    @Test
    void testSetNewCapacity_UpdatesMatchingSchedule() {
        DayOfTheWeek day = DayOfTheWeek.MONDAY;
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);

        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(new ArrayList<>())
                .build();

        scheduleManager.addSchedule(r.getName(), day, start, end, 20);

        r.setNewCapacity(start, end, day, 50);

        LocalTime current = start;
        while (!current.equals(end)) {
            LocalTime finalCurrent = current;
            RestaurantSchedule rs = r.getRestaurantSchedules().stream()
                    .filter(s -> s.getDay() == day && s.getStart().equals(finalCurrent))
                    .findFirst().orElseThrow();

            assertEquals(50, rs.getCapacity(),
                    "La capacité du créneau " + current + " → " + current.plusMinutes(30) + " doit être mise à jour");

            current = current.plusMinutes(30);
        }
    }

    @Test
    void testSetNewCapacity_NoChangeIfNoMatch() {
        DayOfTheWeek day = DayOfTheWeek.MONDAY;
        LocalTime start1 = LocalTime.of(10, 0);
        LocalTime end1 = LocalTime.of(14, 0);
        LocalTime start2 = LocalTime.of(15, 0);
        LocalTime end2 = LocalTime.of(18, 0);

        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(new ArrayList<>())
                .build();

        scheduleManager.addSchedule(r.getName(), day, start1, end1, 20);

        r.setNewCapacity(start2, end2, day, 99);

        LocalTime current = start1;
        while (!current.equals(end1)) {
            LocalTime finalCurrent = current;
            RestaurantSchedule rs = r.getRestaurantSchedules().stream()
                    .filter(s -> s.getDay() == day && s.getStart().equals(finalCurrent))
                    .findFirst().orElseThrow();

            assertEquals(20, rs.getCapacity(),
                    "La capacité du créneau " + current + " → " + current.plusMinutes(30) + " ne doit pas changer");

            current = current.plusMinutes(30);
        }
    }
}
