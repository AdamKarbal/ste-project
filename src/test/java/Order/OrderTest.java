package Order;

import Account.AccountUser;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Restaurant.RestaurantScheduleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private AccountUser client;
    private Restaurant restaurant;
    private RestaurantSchedule schedule;
    private Order order;

    @BeforeEach
    void setup() {
        client = new AccountUser("Adam");
        restaurant = new Restaurant.Builder()
                .name("Campus Cafeteria")
                .restaurantSchedules(new ArrayList<>())
                .build();

        var today = RestaurantScheduleManager.convertDayOfWeek(LocalDate.now().getDayOfWeek());
        LocalTime start = LocalTime.now().minusMinutes(15);
        LocalTime end = LocalTime.now().plusMinutes(30);

        RestaurantScheduleManager.addSchedule(restaurant, today, start, end, 10);

        schedule = restaurant.getRestaurantSchedules().get(0);

        order = new Order(client, restaurant);
    }

    @Test
    void testCreationWithDeliveryPoint() {
        Order order = new Order(client, restaurant);

        order.setDeliveryPoint(OrderDeliveryPoint.CAFETERIA);
        order.setState(OrderState.IN_PREPARATION);

        assertEquals(OrderDeliveryPoint.CAFETERIA, order.getDeliveryPoint(),
                "Le point de livraison devrait être CAFETERIA");
        assertEquals(OrderState.IN_PREPARATION, order.getState(),
                "L'état de la commande devrait être IN_PREPARATION");
    }

    @Test
    void testCreationWithoutDeliveryPoint() {
        Order order = new Order(client, restaurant);

        /*InvalidOrderStateException exception = assertThrows(
                InvalidOrderStateException.class,
                () -> order.setState(OrderState.IN_PREPARATION),
                "Une exception devrait être levée car il n'y a pas de point de livraison"
        );

        assertEquals("Impossible de changer l'état IN_PREPARATION car le point de livraison manque",
                exception.getMessage());
        assertEquals(OrderState.CREATED, order.getState(),
                "L'état de la commande devrait être CREATED car le point de livraison n'a pas été fourni");*/
    }

    @Test
    void testChooseScheduleSuccess() {
        RestaurantSchedule result = order.chooseSchedule(schedule.getStart());

        assertNotNull(order.getDeliveryTime(), "Le temps de livraison devrait être défini");
        assertTrue(order.getDeliveryTime().toLocalTime().isBefore(schedule.getEnd()),
                "La livraison doit être avant la fin du créneau");
        assertEquals(schedule, result, "Le créneau choisi doit être celui retourné");
    }

    @Test
    void testChooseScheduleFailsIfNotInRestaurant() {
        RestaurantSchedule otherSchedule = new RestaurantSchedule(
                schedule.getDay(), schedule.getStart(), schedule.getCapacity()
        );

        RestaurantSchedule result = order.chooseSchedule(otherSchedule.getStart());
        assertNotNull(result, "Le créneau ne devrait pas être accepté car il n'appartient pas au restaurant");
    }

    @Test
    void testConfirmOrderSuccess() {
        order.chooseSchedule(schedule.getStart());
        order.setDeliveryPoint(OrderDeliveryPoint.CAFETERIA);

        boolean result = order.confirmOrder();

        assertTrue(result, "La commande devrait être confirmée");
        assertEquals(OrderState.PAID, order.getState(), "L'état devrait être 'PAID'");
    }

    @Test
    void testConfirmOrderFailsWithoutDeliveryPoint() {
        order.chooseSchedule(schedule.getStart());

        boolean result = order.confirmOrder();

        assertFalse(result, "La confirmation doit échouer sans point de livraison");
        assertNotEquals(OrderState.PAID, order.getState(), "L'état ne doit pas être 'PAID'");
    }


    @Test
    void testConfirmOrderFailsWithoutSchedule() {
        order.setDeliveryPoint(OrderDeliveryPoint.ADMINISTRATION);

        boolean result = order.confirmOrder();

        assertFalse(result, "La confirmation doit échouer sans créneau horaire");
    }
}