package Api.RestaurantSchedule;

import Api.Customer.CustomerHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestaurantScheduleServer {
    public static void main(String[] args) throws IOException {
        int port = 8004;
        String path = "/api/restaurantSchedule/";// port dédié au Restaurant Schedule
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext(path, new CustomerHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantService en ligne sur http://localhost:" + port + path);
    }
}
