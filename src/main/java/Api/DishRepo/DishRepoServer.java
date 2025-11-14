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
        int port = 8004;
        String path = "/api/dishRepo/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        DishRepo dishRepo=new DishRepo();
        dishRepo.save(new Dish(80.0,"Adam karbalito","Ali le dayouth",null,null,null));
        server.createContext(path, new DishRepoHandler(dishRepo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
    }
}
