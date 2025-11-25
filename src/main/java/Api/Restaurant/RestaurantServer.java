package Api.Restaurant;

import Restaurant.RestaurantManager;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class RestaurantServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException {
        int port = 8001;
        String path = "/api/restaurant";
        RestaurantManager restaurantManager = new RestaurantManager();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new RestaurantHandler(restaurantManager));
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("✅ RestaurantService (Business) en ligne sur http://localhost:" + port + path);
        return server;
    }
}