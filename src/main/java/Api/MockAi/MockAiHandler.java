package Api.MockAi;

import Api.Utils.Sender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MockAiHandler implements HttpHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            Sender.sendResponse(exchange, 405, "Méthode non autorisée");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> req = mapper.readValue(body, Map.class);
        String name = (String) req.get("name");
        String domain = (String) req.get("domain");

        List<Map<String,String>> suggestions = generateSuggestions(name, domain);

        Map<String, Object> response = Map.of(
                "suggestions", suggestions
        );

        Sender.sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }

    private List<Map<String,String>> generateSuggestions(String name, String domain) {

        String base = (name == null) ? "" : name.toLowerCase(Locale.ROOT);

        List<String> boloNames = Arrays.asList(
                "La Bolo du Chef",
                "L'Authentique Bolognaise",
                "Spaghetti Passion Bolognaise",
                "La Bolognaise Maison",
                "Tagliatelle Sole Mio Bolognaise"
        );

        List<String> boloDesc = Arrays.asList(
                "Délicieuses pâtes avec sauce bolognaise maison.",
                "Pâtes artisanales servies avec une riche sauce tomate et viande.",
                "Un classique italien revisité avec soin.",
                "Recette généreuse aux saveurs authentiques d'Italie."
        );

        Random r = new Random();

        List<Map<String,String>> out = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            out.add(Map.of(
                    "label", boloNames.get(r.nextInt(boloNames.size())),
                    "description", boloDesc.get(r.nextInt(boloDesc.size()))
            ));
        }
        return out;
    }
}
