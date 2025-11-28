package steps;

import Api.AccountRepo.AccountRepoServer;
import Api.DishRepo.DishRepoServer;
import Api.Order.OrderServer;
import Api.OrderRepo.OrderRepoServer;
import Api.Restaurant.RestaurantServer;
import Api.RestaurantRepo.RestaurantRepoServer;
import Api.RestaurantSchedule.RestaurantScheduleServer;
import com.sun.net.httpserver.HttpServer;
import io.cucumber.java.Before;
import io.cucumber.java.After;

import java.io.IOException;

public class StartServer {
    private HttpServer accountRepo;
    private HttpServer dishRepo;
    private HttpServer orderRepo;
    private HttpServer orderHandler;
    private HttpServer restoRepo;
    private HttpServer restoHandler;
    private HttpServer scheduleManager;

    public static boolean catalogueInit = false;

    @Before(order = 0)
    public void startServers() throws IOException {
        accountRepo = AccountRepoServer.startServer();
        dishRepo = DishRepoServer.startServer();
        orderRepo = OrderRepoServer.startServer();
        orderHandler = OrderServer.startServer();
        restoRepo = RestaurantRepoServer.startServer();
        restoHandler = RestaurantServer.startServer();
        scheduleManager = RestaurantScheduleServer.startServer();

        System.out.println(">>> [StartServer] INFRA démarrée pour ce scénario.");
    }

    @After(order = 0)
    public void stopServers() {
        if (accountRepo != null) accountRepo.stop(0);
        if (dishRepo != null) dishRepo.stop(0);
        if (orderRepo != null) orderRepo.stop(0);
        if (orderHandler != null) orderHandler.stop(0);
        if (restoRepo != null) restoRepo.stop(0);
        if (restoHandler != null) restoHandler.stop(0);
        if (scheduleManager != null) scheduleManager.stop(0);

        System.out.println(">>> [StartServer] INFRA arrêtée pour ce scénario.");
    }
}
