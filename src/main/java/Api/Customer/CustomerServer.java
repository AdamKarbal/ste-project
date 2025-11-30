package Api.Customer;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class CustomerServer {
    public static void main(String[] args) throws IOException {
        int port = 8008; // port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Associe la route /restaurants à ton handler
        server.createContext("/restaurants", new CustomerHandler());
        server.setExecutor(null); // exécution simple
        server.start();

        System.out.println("✅ CustomerService en ligne sur http://localhost:" + port + "/restaurants");
    }
}
