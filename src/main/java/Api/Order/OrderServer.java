package Api.Order;

import Api.Customer.CustomerHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class OrderServer {
    public static void main(String[] args) throws IOException {
        int port = 8000;
        String path = "/api/order/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext(path, new CustomerHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("✅ CustomerService en ligne sur http://localhost:" + port + path);
    }
}
