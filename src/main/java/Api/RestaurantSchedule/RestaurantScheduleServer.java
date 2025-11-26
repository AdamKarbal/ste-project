package Api.RestaurantSchedule;

import Restaurant.RestaurantScheduleManager;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestaurantScheduleServer {

    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException {
        int port = 8007;
        String path = "/api/schedule/";

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        RestaurantScheduleManager manager = new RestaurantScheduleManager();
        RestaurantScheduleHandler handler = new RestaurantScheduleHandler(manager);

        server.createContext(path, handler);
        server.setExecutor(null);
        server.start();

        System.out.println("RestaurantScheduleService en ligne sur http://localhost:" + port + path);
        return server;
    }
}
