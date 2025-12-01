package Api.DishAi;

import Api.Utils.Sender;
import Dish.AiGeneration.DishAiLogic;
import Dish.AiGeneration.DishAiRequest;
import Dish.AiGeneration.DishAiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DishAiHandler implements HttpHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            Sender.sendResponse(exchange, 405, "Méthode non autorisée");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        DishAiRequest req = mapper.readValue(body, DishAiRequest.class);

        // APPEL AU MOCKAI
        URL url = new URL("http://localhost:8011/api/mockai/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        // Transmettre name + image + domain
        String jsonReq = mapper.writeValueAsString(Map.of(
                "name", req.getName(),
                "image", req.getImage(),
                "domain", req.getDomain()
        ));

        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonReq.getBytes(StandardCharsets.UTF_8));
        }

        String aiJson = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> aiMap = mapper.readValue(aiJson, Map.class);

        List<Map<String, String>> suggestions =
                (List<Map<String, String>>) aiMap.get("suggestions");

        // Passage au back métier
        DishAiResult result = DishAiLogic.process(req, suggestions);

        Sender.sendResponse(exchange, 200, mapper.writeValueAsString(result));
    }
}
