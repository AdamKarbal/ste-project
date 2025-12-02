package Api.DishAi;

import Api.Utils.Sender;
import Dish.AiGeneration.DishAiLogic;
import Dish.AiGeneration.DishAiRequest;
import Dish.AiGeneration.DishAiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class DishAiHandler implements HttpHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            Sender.sendResponse(exchange, 405, "Méthode non autorisée");
            return;
        }

        try {
            // ---- 1) Lire la requête utilisateur ----
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            DishAiRequest req = mapper.readValue(body, DishAiRequest.class);

            System.out.println("Request body = " + body);

            if (req.getImage() != null) {
                System.out.println("DishAI: image fournie (longueur=" + req.getImage().length() + ")");
                if (req.getImage().startsWith("data:image")) {
                    System.out.println("DishAI: image détectée au format base64");
                } else if (req.getImage().startsWith("http")) {
                    System.out.println("DishAI: image détectée au format URL");
                }
            }

            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("name", req.getName());
            payload.put("domain", req.getDomain());

            if (req.getImage() != null) {
                payload.put("image", req.getImage());
            }

            String jsonReq = mapper.writeValueAsString(payload);

            HttpRequest mockRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8011/api/mockai/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonReq))
                    .build();

            HttpResponse<String> mockResponse =
                    httpClient.send(mockRequest, HttpResponse.BodyHandlers.ofString());

            if (mockResponse.statusCode() != 200) {
                Sender.sendResponse(exchange, 502,
                        "{\"error\":\"MockAI error, status=" + mockResponse.statusCode() + "\"}");
                return;
            }

            // ---- 3) Récupérer les suggestions ----
            Map<String, Object> aiMap = mapper.readValue(mockResponse.body(), Map.class);
            List<Map<String, String>> suggestions =
                    (List<Map<String, String>>) aiMap.get("suggestions");

            if (suggestions == null || suggestions.isEmpty()) {
                Sender.sendResponse(exchange, 500, "{\"error\":\"No suggestions from MockAI\"}");
                return;
            }

            // ---- 4) Passer au back métier ----
            DishAiResult result = Dish.AiGeneration.DishAiLogic.process(req, suggestions);

            String responseJson = mapper.writeValueAsString(result);
            Sender.sendResponse(exchange, 200, responseJson);

        } catch (Exception e) {
            e.printStackTrace();
            Sender.sendResponse(exchange, 500,
                    "{\"error\":\"Internal DishAI error: " + e.getMessage() + "\"}");
        }
    }
}
