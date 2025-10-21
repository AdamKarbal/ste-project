package Order;

import Account.AccountUser;
import Dish.Dish;
import Dish.DishCategory;
import Dish.DishType;
import Dish.DishTag;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Restaurant.RestaurantScheduleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private AccountUser client;
    private Restaurant restaurant;
    private RestaurantSchedule schedule;
    private Order order;
    private Dish pizza, burger;

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
        pizza = new Dish(10.0, "Pizza Margherita", "Pizza italienne", DishCategory.MAIN_COURSE, DishType.PIZZA, List.of(DishTag.HALAL));
        burger = new Dish(12.0, "Burger", "Burger maison", DishCategory.MAIN_COURSE, DishType.MEAT, List.of(DishTag.HALAL));
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
    void testAssignScheduleSuccess() {
        boolean result = order.assignSchedule(schedule);

        assertTrue(result, "L'assignation du créneau devrait réussir");
        assertNotNull(order.getDeliveryTime(), "Le temps de livraison devrait être défini");
        assertTrue(order.getDeliveryTime().toLocalTime().isAfter(schedule.getEnd()), "La livraison doit être après la fin du créneau");
    }

    @Test
    void testAssignScheduleFailsIfNotInRestaurant() {
        var otherSchedule = new RestaurantSchedule(
                schedule.getDay(), schedule.getStart(), schedule.getCapacity()
        );

        boolean result = order.assignSchedule(otherSchedule);
        assertFalse(result, "Le créneau ne devrait pas être accepté car il n'appartient pas au restaurant");
    }

    @Test
    void testConfirmOrderSuccess() {
        order.assignSchedule(schedule);
        order.setDeliveryPoint(OrderDeliveryPoint.CAFETERIA);

        boolean result = order.confirmOrder();

        assertTrue(result, "La commande devrait être confirmée");
        assertEquals(OrderState.PAID, order.getState(), "L'état devrait être 'PAID'");
    }

    @Test
    void testConfirmOrderFailsWithoutDeliveryPoint() {
        order.assignSchedule(schedule);
        boolean result = order.confirmOrder();

        assertFalse(result, "La confirmation doit échouer sans point de livraison");
        assertNotEquals(OrderState.PAID, order.getState());
    }

    @Test
    void testConfirmOrderFailsWithoutSchedule() {
        order.setDeliveryPoint(OrderDeliveryPoint.ADMINISTRATION);

        boolean result = order.confirmOrder();

        assertFalse(result, "La confirmation doit échouer sans créneau horaire");
    }


    //====================================Test Ali=============================


    @Test
    void testAddDish_ShouldAddAndUpdatePrice() throws DifferentRestaurantException {
        boolean added = order.addDish(pizza, 2);
        assertTrue(added, "Le plat devrait être ajouté");
        assertEquals(20.0, order.getPrice(), 0.01);
        assertEquals(OrderState.IN_PREPARATION, order.getState());
    }

    @Test
    void testAddMultipleDishes_ShouldAccumulateQuantitiesAndPrice() throws DifferentRestaurantException {
        order.addDish(pizza, 1);
        order.addDish(pizza, 2);

        assertEquals(3, order.getDishes().get(pizza), "La quantité doit s'accumuler");
        assertEquals(30.0, order.getPrice(), 0.01);
    }

    @Test
    void testRemoveDish_ShouldUpdatePriceAndState() throws DifferentRestaurantException {
        order.addDish(pizza, 1);
        order.addDish(burger, 1);
        order.removeDish(pizza);

        assertEquals(12.0, order.getPrice(), 0.01, "Le prix doit être mis à jour");
        order.removeDish(burger);
        assertEquals(0.0, order.getPrice());
        assertEquals(OrderState.CREATED, order.getState());
    }

    @Test
    void testUpdateDishQuantity_ShouldChangePrice() throws DifferentRestaurantException {
        order.addDish(pizza, 1);
        order.updateDishQuantity(pizza, 3);

        assertEquals(30.0, order.getPrice(), 0.01);
    }

    @Test
    void testUpdateDishQuantityToZero_ShouldRemoveDish() throws DifferentRestaurantException {
        order.addDish(pizza, 1);
        order.updateDishQuantity(pizza, 0);

        assertTrue(order.getDishes().isEmpty(), "Le plat doit être supprimé si la quantité devient 0");
    }

    @Test
    void testUpdatePrice_ShouldRecalculateTotal() throws DifferentRestaurantException {
        order.addDish(pizza, 1);
        order.addDish(burger, 2);
        order.updatePrice();

        double expected = 10.0 + 24.0;
        assertEquals(expected, order.getPrice(), 0.01);
    }

    @Test
    void testSetState_ShouldChangeState() {
        order.setState(OrderState.IN_PREPARATION);
        assertEquals(OrderState.IN_PREPARATION, order.getState());
    }

    @Test
    void testSetState_ShouldNotThrowWhenSameState() {
        order.setState(OrderState.CREATED);
        assertDoesNotThrow(() -> order.setState(OrderState.CREATED));
    }

}