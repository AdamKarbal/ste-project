package Api.RestaurantRepo;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import static Api.Utils.Sender.sendResponse;

public class RestaurantRepoHandler implements HttpHandler {
    private RestaurantRepo repo;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public RestaurantRepoHandler(RestaurantRepo repo){
        this.repo=repo;
        ApiRegistry.addRoute("GET", "/api/restaurantrepo/{restaurantName}/", this::getRestaurant);
        ApiRegistry.addRoute("GET","/api/restaurantrepo/",this::getAllRestaurants);
        ApiRegistry.addRoute("POST","/api/restaurantrepo/",this::createRestaurant);
        ApiRegistry.addRoute("PUT","/api/restaurantrepo/{restaurantName}/",this::updateRestaurant);
    }
  
    public void getRestaurant(HttpExchange exchange,String... param1) throws IOException {
        String name = URLDecoder.decode(param1[0], StandardCharsets.UTF_8);
        Restaurant restaurantOpt = repo.getRestaurantByName(name);

        if (restaurantOpt==null) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant not found\"}");
            return;
        }
        String response = mapper.writeValueAsString(restaurantOpt);

        sendResponse(exchange, 200, response);
    }

    public void updateRestaurant(HttpExchange exchange, String... params) throws IOException {
        System.out.println("PUT reçu pour: " + params[0]);
        String name = URLDecoder.decode(params[0], StandardCharsets.UTF_8);

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
    public void getAllRestaurants(HttpExchange exchange,String... param) throws IOException{
        try{List<Restaurant> restaurants = repo.getRestaurants();
        if(restaurants==null){
            sendResponse(exchange,404,"Liste des restos vide");
            return;
        }
        String responseJson = mapper.writeValueAsString(restaurants);
        sendResponse(exchange, 200, responseJson);}
        catch(Exception e){
            e.printStackTrace();
            sendResponse(exchange, 500, "Erreur interne du serveur : " + e.getMessage());
        }
    }
    private void createRestaurant(HttpExchange exchange, String... params) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> data = mapper.readValue(body, Map.class);
            String name = (String) data.get("name");
            System.out.println(name);
            if (name == null || name.isBlank()) {
                Sender.sendResponse(exchange, 400, "Field 'name' is required");
                return;
            }
            Restaurant restaurant = repo.addRestaurant(new Restaurant.Builder().name(name).build());
            String json = mapper.writeValueAsString(restaurant);
            Sender.sendResponse(exchange, 201, json);

        } catch (Exception e) {
            e.printStackTrace();
            Sender.sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
        }
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,PUT,POST");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            Sender.sendResponse(exchange, 204, "");
            return;
        }
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
