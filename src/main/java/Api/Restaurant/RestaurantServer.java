package Api.Restaurant;

import Api.Customer.CustomerHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestaurantServer {
    public static void main(String[] args) throws IOException {
        int port = 8003;
        String path = "/api/restaurant/";// port dédié au Restaurant
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext(path, new CustomerHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantService en ligne sur http://localhost:" + port + path);
    }
}
