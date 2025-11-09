package Api.RestaurantRepo;

import Api.Customer.CustomerHandler;
import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class RestaurantRepoServer {
    public static void main(String[] args) throws IOException {
        int port = 8002;
        String path = "/api/restaurantrepo/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        RestaurantRepo repo = new RestaurantRepo(new ArrayList<>());
        repo.addRestaurant(new Restaurant.Builder().name("Tacos").build());
        server.createContext(path, new RestaurantRepoHandler(repo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
    }
}
