package Api.MockAi;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class MockAiServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException {
        int port = 8011;
        String path = "/api/mockai/";

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new MockAiHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("✅ MockAI (3 suggestions) en ligne sur http://localhost:" + port + path);
        return server;
    }
}
