package RestaurantTest;

import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Utils.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Restaurant.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private Restaurant restaurant;

    private static Date d(int day, int month, int year, int hour, int minute) throws Exception {
        return new Date.Builder()
                .day(day).month(month).year(year)
                .hour(hour).minute(minute)
                .build();
    }

    private static Restaurant restaurantWithSingleSlot(Date start, Date end) {
        RestaurantSchedule slot = new RestaurantSchedule(start, end, 10);
        return new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(slot))
                .build();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Resto avec 3 créneaux : 10–14, 18–23, 22–02 (overnight)
        Date startMorning = d(5, 10, 2025, 10, 0);
        Date endMorning   = d(5, 10, 2025, 14, 0);

        Date startEvening = d(5, 10, 2025, 18, 0);
        Date endEvening   = d(5, 10, 2025, 23, 0);

        Date startOvernight = d(5, 10, 2025, 22, 0);
        Date endOvernight   = d(6, 10, 2025, 2, 0);

        RestaurantSchedule morning = new RestaurantSchedule(startMorning, endMorning, 30);
        RestaurantSchedule evening = new RestaurantSchedule(startEvening, endEvening, 50);
        RestaurantSchedule overnight = new RestaurantSchedule(startOvernight, endOvernight, 20);

        restaurant = new Restaurant.Builder()
                .name("Testaurant")
                .restaurantSchedules(List.of(morning, evening, overnight))
                .build();
    }

    // -------- tests "isWithin" via isOpenAt avec un seul créneau --------

    @Test
    void testIsWithin_NormalRangeInside() throws Exception {
        Date start = d(5, 10, 2025, 10, 0);
        Date end   = d(5, 10, 2025, 14, 0);
        Restaurant r = restaurantWithSingleSlot(start, end);

        Date dt = d(5, 10, 2025, 12, 0); // dans 10–14
        assertTrue(r.isOpenAt(dt));
    }

    @Test
    void testIsWithin_NormalRangeOutside() throws Exception {
        Date start = d(5, 10, 2025, 10, 0);
        Date end   = d(5, 10, 2025, 14, 0);
        Restaurant r = restaurantWithSingleSlot(start, end);

        Date dt = d(5, 10, 2025, 15, 0); // hors 10–14
        assertFalse(r.isOpenAt(dt));
    }

    @Test
    void testIsWithin_OvernightInsideBeforeMidnight() throws Exception {
        Date start = d(5, 10, 2025, 22, 0);
        Date end   = d(6, 10, 2025, 2, 0);
        Restaurant r = restaurantWithSingleSlot(start, end);

        Date dt = d(5, 10, 2025, 23, 0); // 23:00 → dans 22–02
        assertTrue(r.isOpenAt(dt));
    }

    @Test
    void testIsWithin_OvernightInsideAfterMidnight() throws Exception {
        Date start = d(5, 10, 2025, 22, 0);
        Date end   = d(6, 10, 2025, 2, 0);
        Restaurant r = restaurantWithSingleSlot(start, end);

        Date dt = d(6, 10, 2025, 1, 0); // 01:00 → dans 22–02
        assertTrue(r.isOpenAt(dt));
    }

    @Test
    void testIsWithin_OvernightOutside() throws Exception {
        Date start = d(5, 10, 2025, 22, 0);
        Date end   = d(6, 10, 2025, 2, 0);
        Restaurant r = restaurantWithSingleSlot(start, end);

        Date dt = d(5, 10, 2025, 18, 0); // 18:00 → hors 22–02
        assertFalse(r.isOpenAt(dt));
    }

    // -------- tests "isOpenAt" sur le resto à 3 créneaux --------

    @Test
    void testIsOpenAt_Open() throws Exception {
        Date current = d(5, 10, 2025, 19, 0); // dans 18–23
        assertTrue(restaurant.isOpenAt(current));
    }

    @Test
    void testIsOpenAt_Closed() throws Exception {
        Date current = d(5, 10, 2025, 15, 0); // entre 14 et 18 → fermé
        assertFalse(restaurant.isOpenAt(current));
    }

    @Test
    void testIsOpenAt_OvernightOpenAfterMidnight() throws Exception {
        Date current = d(6, 10, 2025, 1, 0); // dans 22–02
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
    void testAlreadyScheduled_WhenScheduleExists() throws Exception {
        Date start = d(5, 10, 2025, 10, 0);
        Date end = d(5, 10, 2025, 14, 0);
        RestaurantSchedule s1 = new RestaurantSchedule(start, end, 20);

        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(s1))
                .build();

        assertTrue(r.alreadyScheduled(start, end));
    }

    @Test
    void testAlreadyScheduled_WhenNoMatchingSchedule() throws Exception {
        Date start = d(5, 10, 2025, 10, 0);
        Date end = d(5, 10, 2025, 14, 0);
        RestaurantSchedule s1 = new RestaurantSchedule(start, end, 20);

        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(s1))
                .build();

        Date otherStart = d(5, 10, 2025, 15, 0);
        Date otherEnd = d(5, 10, 2025, 18, 0);

        assertFalse(r.alreadyScheduled(otherStart, otherEnd));
    }

    @Test
    void testAlreadyScheduled_WhenListEmpty() throws Exception {
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of())
                .build();

        Date start = d(5, 10, 2025, 10, 0);
        Date end = d(5, 10, 2025, 14, 0);

        assertFalse(r.alreadyScheduled(start, end));
    }

    @Test
    void testSetNewCapacity_UpdatesMatchingSchedule() throws Exception {
        Date start = d(5, 10, 2025, 10, 0);
        Date end = d(5, 10, 2025, 14, 0);

        RestaurantSchedule s1 = new RestaurantSchedule(start, end, 20);
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(s1))
                .build();

        r.setNewCapacity(start, end, 50);
        assertEquals(50, s1.getCapacity());
    }

    @Test
    void testSetNewCapacity_NoChangeIfNoMatch() throws Exception {
        Date start1 = d(5, 10, 2025, 10, 0);
        Date end1 = d(5, 10, 2025, 14, 0);
        Date start2 = d(5, 10, 2025, 15, 0);
        Date end2 = d(5, 10, 2025, 18, 0);

        RestaurantSchedule s1 = new RestaurantSchedule(start1, end1, 20);
        Restaurant r = new Restaurant.Builder()
                .name("R")
                .restaurantSchedules(List.of(s1))
                .build();

        r.setNewCapacity(start2, end2, 99);
        assertEquals(20, s1.getCapacity());
    }


}
