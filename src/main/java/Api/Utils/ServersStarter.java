package Api.Utils;

import Api.AccountRepo.AccountRepoServer;
import Api.Customer.CustomerServer;
import Api.DishAi.DishAiServer;
import Api.DishRepo.DishRepoServer;
import Api.MockAi.MockAiServer;
import Api.Order.OrderServer;
import Api.OrderRepo.OrderRepoServer;
import Api.Restaurant.RestaurantServer;
import Api.RestaurantRepo.RestaurantRepoServer;
import Api.RestaurantSchedule.RestaurantScheduleServer;

import java.io.IOException;

public class ServersStarter {
    public static void main(String[] args) {
        try {
            AccountRepoServer.startServer();
            CustomerServer.startServer();
            DishRepoServer.startServer();
            OrderServer.startServer();
            OrderRepoServer.startServer();
            RestaurantServer.startServer();
            RestaurantRepoServer.startServer();
            RestaurantScheduleServer.startServer();
            DishAiServer.startServer();
            MockAiServer.startServer();
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("✅ Servers started successfully =======================================================");
    }
}
