package Api.Customer;

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
                    .cuisineType(List.of(CuisineType.ITALIENNE,CuisineType.FRANCAISE))
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
            }
            else if (path.equals("/restaurants/filter")) {
                // Lire les paramètres (support multiple)
                String query = exchange.getRequestURI().getQuery();
                Map<String, List<String>> params = new HashMap<>();

                if (query != null) {
                    for (String p : query.split("&")) {
                        String[] kv = p.split("=");
                        if (kv.length == 2) {
                            params.computeIfAbsent(kv[0], k -> new ArrayList<>()).add(kv[1]);
                        }
                    }
                }

                // Construire dynamiquement les critères
                FilterCriteria.Builder builder = new FilterCriteria.Builder();

                // --- Nom ---
                if (params.containsKey("name")) {
                    builder.nameContains(params.get("name").get(0));
                }

                // --- Cuisine(s) ---
                if (params.containsKey("cuisine")) {
                    for (String c : params.get("cuisine")) {
                        try {
                            CuisineType cuisine = CuisineType.valueOf(c.toUpperCase());
                            builder.cuisine(cuisine);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Type de cuisine invalide ignoré : " + c);
                        }
                    }
                }

                // --- Type(s) d’établissement ---
                if (params.containsKey("type")) {
                    for (String t : params.get("type")) {
                        try {
                            establishmentType type = establishmentType.valueOf(t.toUpperCase());
                            builder.establishment(type);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Type d’établissement invalide ignoré : " + t);
                        }
                    }
                }

                // --- Prix minimum ---
                if (params.containsKey("priceMin")) {
                    try {
                        builder.priceMin(Double.parseDouble(params.get("priceMin").get(0)));
                    } catch (NumberFormatException e) {
                        String errorJson = "{\"error\": \"Valeur invalide pour priceMin : " + params.get("priceMin").get(0) + "\"}";
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
                        builder.priceMax(Double.parseDouble(params.get("priceMax").get(0)));
                    } catch (NumberFormatException e) {
                        String errorJson = "{\"error\": \"Valeur invalide pour priceMax : " + params.get("priceMax").get(0) + "\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }

                // --- Ouvert maintenant ---
                if (params.containsKey("openNow")) {
                    String value = params.get("openNow").get(0).toLowerCase();
                    if (value.equals("true") || value.equals("false")) {
                        builder.openNow(Boolean.parseBoolean(value));
                    } else {
                        String errorJson = "{\"error\": \"Valeur invalide pour openNow : " + value + " (attendu true/false)\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }

                // Construire les critères finaux et filtrer
                FilterCriteria criteria = builder.build();
                System.out.println("🔍 CRITÈRES REÇUS : ");
                System.out.println("name = " + criteria.getNameContains());
                System.out.println("cuisines = " + criteria.getCuisines());
                System.out.println("types = " + criteria.getEstablishments());
                System.out.println("priceMin = " + criteria.getPriceMin());
                System.out.println("priceMax = " + criteria.getPriceMax());
                System.out.println("openNow = " + criteria.getOpenNow());
                restaurants = restaurantService.filter(criteria);
            }
            else if (path.equals("/restaurants/enums/cuisines")) {
                String response = new ObjectMapper().writeValueAsString(CuisineType.values());
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
                return;
            }
            else if (path.equals("/restaurants/enums/types")) {
                String response = new ObjectMapper().writeValueAsString(establishmentType.values());
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
                return;
            }

        } else {
            statusCode = 405; // méthode non autorisée
        }

        // Si on a trouvé des restaurants, on renvoie le JSON
        ObjectMapper mapper = new ObjectMapper();
        String response = mapper.writeValueAsString(restaurants != null ? restaurants : new ArrayList<>());

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
