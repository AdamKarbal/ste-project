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
        restaurantFactory = new RestaurantFactory(restaurantService);
    }

    @Test
    void testAccountManager_CreatesRestaurant() {
        AccountManager accountManager = new AccountManager("Hajj Ali Madandar");
        accountManager.createRestaurant("Hajj Ali Madandar",restaurantFactory);
        Restaurant restaurant = accountManager.getRestaurantManager().getRestaurant();
        assertEquals(1, repo.getRestaurants().size());
        assertEquals("Hajj Ali Madandar", repo.getRestaurants().get(0).getName());
        assertEquals(restaurant, repo.getRestaurants().get(0));
    }


    @Test
    void testAccountManager_MultipleRestaurants() {
        AccountManager manager1 = new AccountManager("Adam");
        AccountManager manager2 = new AccountManager("Ali");
        manager1.createRestaurant("RestoAdam",restaurantFactory);
        manager2.createRestaurant("RestoAli",restaurantFactory);

        assertEquals(2, repo.getRestaurants().size());
        assertTrue(repo.getRestaurants().stream().anyMatch(r -> r.getName().equals("RestoAdam")));
        assertTrue(repo.getRestaurants().stream().anyMatch(r -> r.getName().equals("RestoAli")));
    }
}
