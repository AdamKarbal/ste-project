package Api.Customer;

import Order.OrderManager;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.RestaurantExist;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;



import Restaurant.*;


public class CustomerHandler implements HttpHandler {
    private OrderManager orderManager;
    private final RestaurantService restaurantService = new RestaurantService(new RestaurantRepo(new ArrayList<Restaurant>()));
    public CustomerHandler(){
        try {
            Restaurant r1 = new Restaurant.Builder()
                    .name("Cafeteria du Campus")
                    .establishmentType(establishmentType.FAST_FOOD)
                    .cuisineType(List.of(CuisineType.AMERICAINE))
                    .dishes(new ArrayList<>())
                    .restaurantSchedules(new ArrayList<>())
                    .build();

            Restaurant r2 = new Restaurant.Builder()
                    .name("Pizza Express")
                    .establishmentType(establishmentType.RESTAURANT)
                    .cuisineType(List.of(CuisineType.ITALIENNE))
                    .dishes(new ArrayList<>())
                    .restaurantSchedules(new ArrayList<>())
                    .build();

            restaurantService.addRestaurant(r1);
            restaurantService.addRestaurant(r2);
        } catch (RestaurantExist e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        System.out.println("Requête reçue : " + method + " " + path);

        List<Restaurant> restaurants = null;
        int statusCode = 200;

        if ("GET".equalsIgnoreCase(method)) {
            if (path.equals("/restaurants")) {
                restaurants = restaurantService.getAllRestaurants();
            } else if (path.equals("/restaurants/filter")) {
                // 1️⃣ Lire les paramètres de la requête
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = new HashMap<>();

                if (query != null) {
                    for (String p : query.split("&")) {
                        String[] kv = p.split("=");
                        if (kv.length == 2) params.put(kv[0], kv[1]);
                    }
                }

                // 2️⃣ Construire dynamiquement les critères
                FilterCriteria.Builder builder = new FilterCriteria.Builder();

                // --- Nom ---
                if (params.containsKey("name")) {
                    builder.nameContains(params.get("name"));
                }

                // --- Cuisine ---
                if (params.containsKey("cuisine")) {
                    try {
                        CuisineType cuisine = CuisineType.valueOf(params.get("cuisine").toUpperCase());
                        builder.cuisine(cuisine);
                    } catch (IllegalArgumentException e) {
                        String errorJson = "{\"error\": \"Type de cuisine invalide : " + params.get("cuisine") + "\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }

                // --- Type d’établissement ---
                if (params.containsKey("type")) {
                    try {
                        establishmentType type = establishmentType.valueOf(params.get("type").toUpperCase());
                        builder.establishment(type);
                    } catch (IllegalArgumentException e) {
                        String errorJson = "{\"error\": \"Type d’établissement invalide : " + params.get("type") + "\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }

                // --- Prix minimum ---
                if (params.containsKey("priceMin")) {
                    try {
                        builder.priceMin(Double.parseDouble(params.get("priceMin")));
                    } catch (NumberFormatException e) {
                        String errorJson = "{\"error\": \"Valeur invalide pour priceMin : " + params.get("priceMin") + "\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }

                // --- Prix maximum ---
                if (params.containsKey("priceMax")) {
                    try {
                        builder.priceMax(Double.parseDouble(params.get("priceMax")));
                    } catch (NumberFormatException e) {
                        String errorJson = "{\"error\": \"Valeur invalide pour priceMax : " + params.get("priceMax") + "\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }

                // --- Ouvert maintenant ---
                if (params.containsKey("openNow")) {
                    String value = params.get("openNow").toLowerCase();
                    if (value.equals("true") || value.equals("false")) {
                        builder.openNow(Boolean.parseBoolean(value));
                    } else {
                        String errorJson = "{\"error\": \"Valeur invalide pour openNow : " + params.get("openNow") + " (attendu true/false)\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }

                // 3️⃣ Construire les critères finaux et filtrer
                FilterCriteria criteria = builder.build();
                restaurants = restaurantService.filter(criteria);

            }
        } else {
            statusCode = 405; // méthode non autorisée
        }


        // Si on a trouvé des restaurants, on renvoie le JSON
        ObjectMapper mapper = new ObjectMapper();
        String response = mapper.writeValueAsString(restaurants != null ? restaurants : new ArrayList<>());

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }



}