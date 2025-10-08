package Order;

import Account.AccountUser;
import Restaurant.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private AccountUser client;
    private Restaurant restaurant;

    @BeforeEach
    void setup() {
        client = new AccountUser();
        restaurant = new Restaurant.Builder()
                .name("Campus Cafeteria")
                .build();
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

        InvalidOrderStateException exception = assertThrows(
                InvalidOrderStateException.class,
                () -> order.setState(OrderState.IN_PREPARATION),
                "Une exception devrait être levée car il n'y a pas de point de livraison"
        );

        assertEquals("Impossible de changer l'état IN_PREPARATION car le point de livraison manque",
                exception.getMessage());
        assertEquals(OrderState.CREATED, order.getState(),
                "L'état de la commande devrait être CREATED car le point de livraison n'a pas été fourni");
    }
}