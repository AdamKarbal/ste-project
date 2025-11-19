package Api.RestaurantRepo;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Restaurant.RestaurantRepo;
import Restaurant.Restaurant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;

import static Api.Utils.Sender.sendResponse;

public class RestaurantRepoHandler implements HttpHandler {
    private RestaurantRepo repo;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public RestaurantRepoHandler(RestaurantRepo repo){
        this.repo=repo;
        String pathRegex = "/api/restaurantrepo/([^/]+)";
        ApiRegistry.addRoute("GET", pathRegex, this::getRestaurant);
        ApiRegistry.addRoute("PUT", pathRegex, this::updateRestaurant);
    }
    public void getRestaurant(HttpExchange exchange,String param1) throws IOException {
        System.out.println("Get reçu");
        String name = URLDecoder.decode(param1, StandardCharsets.UTF_8);
        Restaurant restaurantOpt = repo.getRestaurantByName(name);

        if (restaurantOpt==null) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant not found\"}");
            return;
        }
        String response = mapper.writeValueAsString(restaurantOpt);

        sendResponse(exchange, 200, response);
    }

    public void updateRestaurant(HttpExchange exchange, String restaurantName) throws IOException {
        System.out.println("PUT reçu pour: " + restaurantName);
        String name = URLDecoder.decode(restaurantName, StandardCharsets.UTF_8);

        try (InputStream is = exchange.getRequestBody()) {
            String jsonBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Restaurant updatedRestaurant = mapper.readValue(jsonBody, Restaurant.class);
            boolean exists = repo.updateRestaurant(name, updatedRestaurant);

            if (exists) {
                sendResponse(exchange, 200, "{\"message\":\"Restaurant " + name + " updated\"}");
            } else {
                repo.addRestaurant(updatedRestaurant);
                sendResponse(exchange, 201, "{\"message\":\"Restaurant " + name + " created\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Invalid request body: " + e.getMessage() + "\"}");
        }
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,PUT");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        for (RouteInfo route : ApiRegistry.getRoutes()) {
            if (route.matches(requestMethod, requestPath)) {
                Matcher matcher = route.getPathMatcher(requestPath);
                String param = "";
                if (matcher.find() && matcher.groupCount() > 0) {
                    param = matcher.group(1);
                }

                route.getHandler().handle(exchange, param);
                return;
            }
        }
    }



}
