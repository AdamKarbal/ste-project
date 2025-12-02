package Dish.AiGeneration;

import Api.DishAi.DishAiServer;
import Api.MockAi.MockAiServer;
import Api.Utils.Requester;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class DishAiTest {
    private static HttpServer serverDishAi;
    private static HttpServer serverMockAi;

    private static List<HttpServer> servers;

    @BeforeAll
    static void setup() {
        try {
            serverMockAi = MockAiServer.startServer();
            serverDishAi = DishAiServer.startServer();
            servers = List.of(serverDishAi, serverMockAi);
        } catch (Exception e) {
            System.out.println("Servers could not be started: " + e.getMessage());
        }
    }

    @AfterAll
    public static void stopServers() {
        if (servers != null) {
            for (HttpServer server : servers) {
                if (server != null) {
                    server.stop(0);
                }
            }
        }
    }

    @Test
    public void testDishAi() {
        // 🔹 Image URL classique
        String imgUrl = "https://example.com/images/pate-bolo.png";

        // 🔹 Image Base64 simulée
        String fakeBinary = "image-fictive";
        String imgBase64 = "data:image/png;base64," +
                Base64.getEncoder().encodeToString(fakeBinary.getBytes(StandardCharsets.UTF_8));

        List<DishAiResult> tests = List.of(
                Requester.generateDishFromAI("pate bolo", imgUrl, "italian"),
                Requester.generateDishFromAI("pate simple", imgBase64, "italian"),
                Requester.generateDishFromAI("pate bolo", null, "japanese"),
                Requester.generateDishFromAI("pate bolo de merde", null, "default"),
                Requester.generateDishFromAI("", null, "italian")
        );

        for (DishAiResult r : tests) {
            System.out.println("----- TEST -----");
            System.out.println("Status: " + r.getStatus());
            System.out.println("Score : " + r.getConfidence());
            if (r.getDish() != null) {
                System.out.println("Dish : " + r.getDish().getName());
                System.out.println("Type : " + r.getDish().getType());
            }
            System.out.println();
        }
    }
}
