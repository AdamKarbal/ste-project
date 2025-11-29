package Api.RestaurantRepo;

import Api.Customer.CustomerHandler;
import Dish.Dish;
import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import com.sun.net.httpserver.HttpServer;
import Dish.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepoServer {

    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException {
        int port = 8002;
        String path = "/api/restaurantrepo/";
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        RestaurantRepo repo = new RestaurantRepo(new ArrayList<>());
        server.createContext(path, new RestaurantRepoHandler(repo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
        return server;
    }
}