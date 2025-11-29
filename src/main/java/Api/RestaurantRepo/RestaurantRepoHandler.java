package Api.RestaurantRepo;
import Restaurant.Enum.establishmentType;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static Api.Utils.Sender.sendResponse;

public class RestaurantRepoHandler implements HttpHandler {
    private RestaurantRepo repo;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public RestaurantRepoHandler(RestaurantRepo repo) {
        this.repo = repo;
        ApiRegistry.addRoute("GET", "/api/restaurantrepo/{restaurantName}/", this::getRestaurant);
        ApiRegistry.addRoute("GET", "/api/restaurantrepo/", this::getAllRestaurants);
        ApiRegistry.addRoute("POST", "/api/restaurantrepo/", this::createRestaurant);
        ApiRegistry.addRoute("PUT", "/api/restaurantrepo/{restaurantName}/", this::updateRestaurant);
        ApiRegistry.addRoute("DELETE", "/api/restaurantrepo/", this::clearAll);
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
        String pathName = URLDecoder.decode(params[0], StandardCharsets.UTF_8);

        try (InputStream is = exchange.getRequestBody()) {
            String jsonBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            if (jsonBody == null || jsonBody.isBlank()) {
                Sender.sendResponse(exchange, 400, "{\"error\":\"Request body is empty\"}");
                return;
            }

            Restaurant updatedRestaurant = mapper.readValue(jsonBody, Restaurant.class);

            // Vérif / alignement du nom
            String bodyName = updatedRestaurant.getName();
            if (bodyName == null || bodyName.isBlank()) {
                // si le body n’a pas de name, on prend celui de l’URL
                updatedRestaurant.setName(pathName);
            } else if (!bodyName.equals(pathName)) {
                // incohérence entre l’URL et le body -> 400
                Sender.sendResponse(
                        exchange,
                        400,
                        "{\"error\":\"Restaurant name in path ('" + pathName +
                                "') does not match body ('" + bodyName + "')\"}"
                );
                return;
            }

            // Sécurisation des collections / type, comme dans createRestaurant
            if (updatedRestaurant.getDishes() == null) {
                updatedRestaurant.setDishes(new ArrayList<>());
            }
            if (updatedRestaurant.getRestaurantSchedules() == null) {
                updatedRestaurant.setRestaurantSchedules(new ArrayList<>());
            }
            if (updatedRestaurant.getCuisineTypes() == null) {
                updatedRestaurant.setCuisineTypes(new ArrayList<>());
            }
            if (updatedRestaurant.getEstablishmentType() == null) {
                updatedRestaurant.setEstablishmentType(establishmentType.DEFAULT);
            }

            // Mise à jour ou création
            boolean existed = repo.updateRestaurant(pathName, updatedRestaurant);

            String json = mapper.writeValueAsString(updatedRestaurant);
            int status = existed ? 200 : 201;
            Sender.sendResponse(exchange, status, json);

            /*System.out.println("\n[DEBUG updateRestaurant] PATH = " + pathName);
            System.out.println("[DEBUG updateRestaurant] BODY reçu = " + jsonBody);
            System.out.println("[DEBUG updateRestaurant] Restaurant désérialisé = " + updatedRestaurant);
            System.out.println("[DEBUG updateRestaurant] Dishes count = " + updatedRestaurant.getDishes().size());*/


        } catch (Exception e) {
            e.printStackTrace();
            Sender.sendResponse(exchange, 500,
                    "{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
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

            Restaurant restaurant = mapper.readValue(body, Restaurant.class);

            String name = restaurant.getName();
            if (name == null || name.isBlank()) {
                Sender.sendResponse(exchange, 400, "Field 'name' is required");
                return;
            }

            if (restaurant.getDishes() == null) {
                restaurant.setDishes(new ArrayList<>());
            }
            if (restaurant.getRestaurantSchedules() == null) {
                restaurant.setRestaurantSchedules(new ArrayList<>());
            }
            if (restaurant.getCuisineTypes() == null) {
                restaurant.setCuisineTypes(new ArrayList<>());
            }
            if (restaurant.getEstablishmentType() == null) {
                restaurant.setEstablishmentType(establishmentType.DEFAULT);
            }

            Restaurant saved = repo.addRestaurant(restaurant);

            String json = mapper.writeValueAsString(saved);
            Sender.sendResponse(exchange, 201, json);

        } catch (Exception e) {
            e.printStackTrace();
            Sender.sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
        }
    }

    private void clearAll(HttpExchange ex, String... params) throws IOException {
        repo.clear();
        Sender.sendResponse(ex, 200, "{\"ok\": true}");
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
