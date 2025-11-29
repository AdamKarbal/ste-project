package Order;

import Account.AccountUser;
import Api.AccountRepo.AccountRepoServer;
import Api.DishRepo.DishRepoServer;
import Api.Order.OrderServer;
import Api.OrderRepo.OrderRepoServer;
import Api.RestaurantRepo.RestaurantRepoServer;
import Api.RestaurantSchedule.RestaurantScheduleServer;
import Api.Utils.Requester;
import Dish.Dish;
import Dish.DishCategory;
import Dish.DishCreateDTO;
import Dish.DishTag;
import Dish.DishType;
import Restaurant.Restaurant;
import Restaurant.RestaurantSchedule;
import Restaurant.RestaurantScheduleManager;
import com.sun.net.httpserver.HttpServer;
import io.cucumber.java.Before;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private AccountUser client;
    private Restaurant restaurant;
    private Order order;
    private Dish pizza, burger;
    private RestaurantScheduleManager scheduleManager;

    private static HttpServer accountRepo;
    private static HttpServer dishRepo;
    private static HttpServer orderRepo;
    private static HttpServer orderHandler;
    private static HttpServer restoRepo;
    private static HttpServer scheduleServer;
    private static List<HttpServer> servers;

    @BeforeAll
    public static void setUpServers() throws IOException {
        try{
        accountRepo = AccountRepoServer.startServer();
        dishRepo = DishRepoServer.startServer();
        orderRepo = OrderRepoServer.startServer();
        orderHandler = OrderServer.startServer();
        restoRepo = RestaurantRepoServer.startServer();
        scheduleServer = RestaurantScheduleServer.startServer();
        }
        catch (java.net.BindException e) {
            System.out.println("déjà utilisée");
        }

        servers = List.of(accountRepo, dishRepo, orderRepo, orderHandler, restoRepo,scheduleServer);
    }

    @AfterAll
    public static void stopServers() {
        if (servers != null) {
            for (HttpServer server : servers) {
                if (server != null) {
                    server.stop(0);
                }
            }
        }
    }


    @BeforeEach
    void setup() {
        client = new AccountUser("Adam");
        restaurant = new Restaurant.Builder()
                .name("CampusCafeteria")
                .restaurantSchedules(new ArrayList<>())
                .build();

        Requester.createRestaurant(restaurant);
      
        scheduleManager = new RestaurantScheduleManager();

        var today = scheduleManager.convertDayOfWeek(LocalDate.now().getDayOfWeek());
      
        LocalTime start = LocalTime.now().minusMinutes(15);
        LocalTime end = LocalTime.now().plusMinutes(30);

        Requester.addSchedule(restaurant.getName(), today, start, end, 10);

        order = new Order(client.getId(), restaurant.getName());

        pizza = Requester.createDish(
                new Dish(10.0, "Pizza Margherita", "Pizza italienne",
                        DishCategory.MAIN_COURSE, DishType.PIZZA,
                        List.of(DishTag.HALAL)).toCreateDTO()
        );

        burger = Requester.createDish(
                new Dish(12.0, "Burger", "Burger maison",
                        DishCategory.MAIN_COURSE, DishType.MEAT,
                        List.of(DishTag.HALAL)).toCreateDTO()
        );
    }

    @Test
    void testCreationWithDeliveryPoint() {
        Order order = new Order(client.getId(), restaurant.getName());
        order.setDeliveryPoint(OrderDeliveryPoint.CAFETERIA);
        order.setState(OrderState.IN_PREPARATION);

        assertEquals(OrderDeliveryPoint.CAFETERIA, order.getDeliveryPoint());
        assertEquals(OrderState.IN_PREPARATION, order.getState());
    }

    @Test
    void testCreationWithoutDeliveryPoint() {
    }

    /*@Test
    void testChooseScheduleSuccess() {
        RestaurantSchedule result = order.chooseSchedule(schedule.getStart());
        assertNotNull(order.getDeliveryTime());
        assertTrue(order.getDeliveryTime().toLocalTime().isBefore(schedule.getEnd()));
        assertEquals(schedule, result);
    }*/

    /*@Test
    void testChooseScheduleFailsIfNotInRestaurant() {
        RestaurantSchedule otherSchedule = new RestaurantSchedule(
                schedule.getDay(), schedule.getStart(), schedule.getCapacity()
        );

        RestaurantSchedule result = order.chooseSchedule(otherSchedule.getStart());
        assertNotNull(result);
    }*/

    /*@Test
    void testConfirmOrderSuccess() {
        order.chooseSchedule(schedule.getStart());
        order.setDeliveryPoint(OrderDeliveryPoint.CAFETERIA);

        boolean result = order.confirmOrder();

        assertTrue(result);
        assertEquals(OrderState.PAID, order.getState());
    }*/

    /*@Test // pr ce test faut que y ait le service schedule
    void testConfirmOrderFailsWithoutDeliveryPoint() {
        order.chooseSchedule(schedule.getStart());
        boolean result = order.confirmOrder();

        assertFalse(result);
        assertNotEquals(OrderState.PAID, order.getState());
    }*/

    @Test
    void testConfirmOrderFailsWithoutSchedule() {
        order.setDeliveryPoint(OrderDeliveryPoint.ADMINISTRATION);
        boolean result = order.confirmOrder();

        assertFalse(result);
    }

    @Test
    void testAddDish_ShouldAddAndUpdatePrice() throws DifferentRestaurantException {
        boolean added = order.addDish(pizza.getId(), 2);
        assertTrue(added);
        assertEquals(20.0, order.getPrice(), 0.01);
        assertEquals(OrderState.IN_PREPARATION, order.getState());
    }

    @Test
    void testAddMultipleDishes_ShouldAccumulateQuantitiesAndPrice() throws DifferentRestaurantException {
        order.addDish(pizza.getId(), 1);
        order.addDish(pizza.getId(), 2);

        assertEquals(3, order.getDishes().get(pizza.getId()));
        assertEquals(30.0, order.getPrice(), 0.01);
    }

    @Test
    void testRemoveDish_ShouldUpdatePriceAndState() throws DifferentRestaurantException {
        order.addDish(pizza.getId(), 1);
        order.addDish(burger.getId(), 1);
        order.removeDish(pizza.getId());

        assertEquals(12.0, order.getPrice(), 0.01);

        order.removeDish(burger.getId());
        assertEquals(0.0, order.getPrice());
        assertEquals(OrderState.CREATED, order.getState());
    }

    @Test
    void testUpdateDishQuantity_ShouldChangePrice() throws DifferentRestaurantException {
        order.addDish(pizza.getId(), 1);
        order.updateDishQuantity(pizza.getId(), 3);

        assertEquals(30.0, order.getPrice(), 0.01);
    }

    @Test
    void testUpdateDishQuantityToZero_ShouldRemoveDish() throws DifferentRestaurantException {
        order.addDish(pizza.getId(), 1);
        order.updateDishQuantity(pizza.getId(), 0);

        assertTrue(order.getDishes().isEmpty());
    }

    @Test
    void testUpdatePrice_ShouldRecalculateTotal() throws DifferentRestaurantException {
        order.addDish(pizza.getId(), 1);
        order.addDish(burger.getId(), 2);
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
