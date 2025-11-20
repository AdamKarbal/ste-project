package Account;

import Api.RestaurantRepo.RestaurantRepoServer;
import Restaurant.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerTest {

    private static HttpServer server;
    private static boolean serverStartedByThisClass = false;

    // Objets "Dummy" (Bouchons) pour satisfaire le constructeur de la Factory
    private RestaurantRepo dummyRepo;
    private RestaurantService dummyService;
    private RestaurantFactory restaurantFactory;

    @BeforeAll
    static void startInfrastructure() throws IOException {
        try {
            server = RestaurantRepoServer.startServer();
            serverStartedByThisClass = true;
        } catch (java.net.BindException e) {
            System.out.println("⚠️ Serveur déjà en cours (BindException), on continue...");
        }
    }

    @AfterAll
    static void stopInfrastructure() {
        if (server != null && serverStartedByThisClass) {
            server.stop(0);
        }
    }

    @BeforeEach
    void setUp() {
        // On crée des objets qui ne servent à rien d'autre qu'à éviter l'erreur de compilation
        dummyRepo = new RestaurantRepo(new ArrayList<>());
        dummyService = new RestaurantService(dummyRepo);

        // On initialise la factory AVEC le service (respect de ta contrainte)
        restaurantFactory = new RestaurantFactory(dummyService);
    }

    @Test
    void testAccountManager_CreatesRestaurant() {
        AccountManager accountManager = new AccountManager("Hajj Ali Madandar");

        // Utilise la factory (qui crée l'objet) et le manager interne (qui sauvegarde via HTTP)
        accountManager.createRestaurant("Hajj Ali Madandar", restaurantFactory);

        // On vérifie sur le serveur (via le manager)
        Restaurant restaurant = accountManager.getRestaurant();

        assertNotNull(restaurant, "Le restaurant n'a pas été créé sur le serveur");
        assertEquals("Hajj Ali Madandar", restaurant.getName());
    }

    @Test
    void testAccountManager_MultipleRestaurants() {
        AccountManager manager1 = new AccountManager("Adam");
        AccountManager manager2 = new AccountManager("Ali");

        manager1.createRestaurant("RestoAdam", restaurantFactory);
        manager2.createRestaurant("RestoAli", restaurantFactory);

        Restaurant r1 = manager1.getRestaurant();
        Restaurant r2 = manager2.getRestaurant();

        assertNotNull(r1);
        assertEquals("RestoAdam", r1.getName());

        assertNotNull(r2);
        assertEquals("RestoAli", r2.getName());
    }
}