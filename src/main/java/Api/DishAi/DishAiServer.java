package Api.DishAi;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class DishAiServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException {
        int port = 8010;
        String path = "/api/dishai/";

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new DishAiHandler());
        server.start();

        System.out.println("✅ DishAiService en ligne sur http://localhost:" + port + path);
        return server;
    }
}
