package Api.DishRepo;

import Api.OrderRepo.OrderRepoHandler;
import Dish.Dish;
import Dish.DishRepo;
import Order.OrderRepo;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DishRepoServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException{
        int port = 8004;
        String path = "/api/dishRepo/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        DishRepo repo = new DishRepo();
        server.createContext(path, new DishRepoHandler(repo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
        return server;
    }
}
