package Account;

import Restaurant.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerTest {

    private RestaurantRepo repo;
    private RestaurantService restaurantService;
    private RestaurantFactory restaurantFactory;

    @BeforeEach
    void setUp() {
        repo = new RestaurantRepo(new ArrayList<>());
        restaurantService = new RestaurantService(repo);
    }

    @Test
    void testAccountManager_CreatesRestaurant() {
        AccountManager accountManager = new AccountManager("Hajj Ali Madandar");
        accountManager.createRestaurant("Hajj Ali Madandar",);
        Restaurant restaurant = accountManager.getRestaurantManager().getRestaurant();
        assertEquals(1, repo.getRestaurants().size());
        assertEquals("Hajj Ali Madandar", repo.getRestaurants().get(0).getName());
        assertEquals(restaurant, repo.getRestaurants().get(0));
    }

    @Test
    void testAccountManager_DuplicateRestaurantIgnored() {
        // Crée un premier restaurant
        new AccountManager("Hajj Ali Madandar");

        // Essaye de créer un deuxième restaurant avec le même nom
        AccountManager secondManager = new AccountManager("Hajj Ali Madandar");

        // Le repo ne doit contenir qu’un seul restaurant
        assertEquals(1, repo.getRestaurants().size());
        assertEquals("Hajj Ali Madandar", repo.getRestaurants().get(0).getName());
    }


    @Test
    void testAccountManager_MultipleRestaurants() {
        AccountManager manager1 = new AccountManager("Adam");
        AccountManager manager2 = new AccountManager("Ali");

        assertEquals(2, repo.getRestaurants().size());
        assertTrue(repo.getRestaurants().stream().anyMatch(r -> r.getName().equals("Adam")));
        assertTrue(repo.getRestaurants().stream().anyMatch(r -> r.getName().equals("Ali")));
    }
}
