package Restaurant;

import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.cuisineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private Restaurant restaurant;

    private static Restaurant restaurantWithSingleSlot(DayOfTheWeek dayOfTheWeek, LocalTime start, LocalTime end) {
        RestaurantSchedule slot = new RestaurantSchedule(dayOfTheWeek, start, end, 10);
        return new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(slot))
                .build();
    }

    @BeforeEach
    void setUp() {
        // Resto avec 3 créneaux : 10–14, 18–23, 22–02 (overnight)
        RestaurantSchedule morning = new RestaurantSchedule(DayOfTheWeek.SUNDAY, LocalTime.of(10, 0), LocalTime.of(14, 0), 30);
        RestaurantSchedule evening = new RestaurantSchedule(DayOfTheWeek.SUNDAY, LocalTime.of(18, 0), LocalTime.of(23, 0), 50);
        RestaurantSchedule overnight = new RestaurantSchedule(DayOfTheWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(2, 0), 20);

        restaurant = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(List.of(morning, evening, overnight))
                .build();
    }

    // -------- tests "isWithin" via isOpenAt avec un seul créneau --------

    @Test
    void testIsWithin_NormalRangeInside() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));

        LocalDateTime dt = LocalDateTime.of(2025, 10, 5, 12, 0); // 12h → dans 10–14
        assertTrue(r.isOpenAt(dt));
    }

    @Test
    void testIsWithin_NormalRangeOutside() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));

        LocalDateTime dt = LocalDateTime.of(2025, 10, 5, 15, 0); // hors 10–14
        assertFalse(r.isOpenAt(dt));
    }

    @Test
    void testIsWithin_OvernightInsideBeforeMidnight() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(2, 0));

        LocalDateTime dt = LocalDateTime.of(2025, 10, 5, 23, 0); // 23h → dans 22–02
        assertTrue(r.isOpenAt(dt));
    }

    @Test
    void testIsWithin_OvernightInsideAfterMidnight() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(2, 0));

        LocalDateTime dt = LocalDateTime.of(2025, 10, 6, 1, 0); // 1h du matin → toujours ouvert
        assertTrue(r.isOpenAt(dt));
    }

    @Test
    void testIsWithin_OvernightOutside() {
        Restaurant r = restaurantWithSingleSlot(DayOfTheWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(2, 0));

        LocalDateTime dt = LocalDateTime.of(2025, 10, 5, 18, 0); // 18h → avant ouverture
        assertFalse(r.isOpenAt(dt));
    }

    // -------- tests "isOpenAt" sur le resto à 3 créneaux --------

    @Test
    void testIsOpenAt_Open() {
        LocalDateTime current = LocalDateTime.of(2025, 10, 5, 19, 0); // dans 18–23
        assertTrue(restaurant.isOpenAt(current));
    }

    @Test
    void testIsOpenAt_Closed() {
        LocalDateTime current = LocalDateTime.of(2025, 10, 5, 15, 0); // entre 14 et 18 → fermé
        assertFalse(restaurant.isOpenAt(current));
    }

    @Test
    void testIsOpenAt_OvernightOpenAfterMidnight() {
        LocalDateTime current = LocalDateTime.of(2025, 10, 6, 1, 0); // 1h → dans 22–02
        assertTrue(restaurant.isOpenAt(current));
    }

    @Test
    void testAlreadyCuisineType_WhenTypeAlreadyPresent() {
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .cuisineType(List.of(cuisineType.ITALIENNE, cuisineType.JAPONAISE))
                .build();

        assertTrue(r.alreadyCuisineType(cuisineType.ITALIENNE));
    }

    @Test
    void testAlreadyCuisineType_WhenTypeNotPresent() {
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .cuisineType(List.of(cuisineType.ITALIENNE, cuisineType.JAPONAISE))
                .build();

        assertFalse(r.alreadyCuisineType(cuisineType.MEXICAINE));
    }

    @Test
    void testAlreadyScheduled_WhenScheduleExists() {
        DayOfTheWeek day = DayOfTheWeek.MONDAY;
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);
        RestaurantSchedule s1 = new RestaurantSchedule(day, start, end, 20);

        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(s1))
                .build();

        assertTrue(r.alreadyScheduled(start, end, day));
    }

    @Test
    void testAlreadyScheduled_WhenNoMatchingSchedule() {
        DayOfTheWeek day = DayOfTheWeek.MONDAY;
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 0);
        RestaurantSchedule s1 = new RestaurantSchedule(day, start, end, 20);

        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(s1))
                .build();

        // test d'un créneau différent (15:00-18:00 le même jour)
        LocalTime otherStart = LocalTime.of(15, 0);
        LocalTime otherEnd = LocalTime.of(18, 0);

        assertFalse(r.alreadyScheduled(otherStart, otherEnd, day));
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

        RestaurantSchedule s1 = new RestaurantSchedule(day, start, end, 20);
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(s1))
                .build();

        // on appelle la méthode qui met à jour la capacité pour le créneau donné
        r.setNewCapacity(start, end, day, 50);

        assertEquals(50, s1.getCapacity());
    }

    @Test
    void testSetNewCapacity_NoChangeIfNoMatch() {
        DayOfTheWeek day = DayOfTheWeek.MONDAY;
        LocalTime start1 = LocalTime.of(10, 0);
        LocalTime end1 = LocalTime.of(14, 0);
        LocalTime start2 = LocalTime.of(15, 0);
        LocalTime end2 = LocalTime.of(18, 0);

        RestaurantSchedule s1 = new RestaurantSchedule(day, start1, end1, 20);
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(s1))
                .build();

        // Tentative de mise à jour sur un créneau différent (pas de match)
        r.setNewCapacity(start2, end2, day, 99);

        assertEquals(20, s1.getCapacity());
    }
}
