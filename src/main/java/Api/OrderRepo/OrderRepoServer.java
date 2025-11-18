package Api.OrderRepo;

import Api.RestaurantRepo.RestaurantRepoHandler;
import Order.OrderRepo;
import Restaurant.Enum.establishmentType;
import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class OrderRepoServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }
    public static HttpServer startServer() throws IOException {
        int port = 8003;
        String path = "/api/orderRepo/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        OrderRepo orderRepo=new OrderRepo();
        server.createContext(path, new OrderRepoHandler(orderRepo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
        return  server;
    }
}
