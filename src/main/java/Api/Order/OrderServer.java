package Api.Order;

import Api.Customer.CustomerHandler;
import Order.OrderManager;
import Order.OrderRepo;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class OrderServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }
    public static HttpServer startServer() throws IOException {
        int port = 8000;
        String path = "/api/order/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new OrderHandler(new OrderManager()));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ CustomerService en ligne sur http://localhost:" + port + path);
        return server;
    }
}
