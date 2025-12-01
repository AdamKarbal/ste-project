package Api.Customer;

import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.RestaurantExist;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import Restaurant.*;

public class CustomerHandler implements HttpHandler {
    private final RestaurantService restaurantService = new RestaurantService();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    public CustomerHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        System.out.println("Requête reçue : " + method + " " + path);

        // --- Gestion CORS (préflight) ---
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        List<Restaurant> restaurants = null;
        int statusCode = 200;

        if ("GET".equalsIgnoreCase(method)) {
            if (path.equals("/restaurants")) {
                restaurants = restaurantService.getAllRestaurants();
                System.out.println(restaurants);
            } else if (path.equals("/restaurants/filter")) {

                // Lecture des paramètres multiples
                String query = exchange.getRequestURI().getQuery();
                Map<String, List<String>> params = new HashMap<>();

                if (query != null) {
                    for (String p : query.split("&")) {
                        String[] kv = p.split("=");
                        if (kv.length == 2) {
                            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                            String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                            params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                        }
                    }
                }

                // Construction dynamique des critères
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
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
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
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
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
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }

                // --- Ouvert à une heure / jour précis ---
                if (params.containsKey("openAt")) {
                    String raw = params.get("openAt").get(0);
                    try {
                        int idx = raw.indexOf('-');
                        if (idx > 0 && idx < raw.length() - 1) {
                            String dayStr = raw.substring(0, idx).trim().toUpperCase();
                            String timeStr = raw.substring(idx + 1).trim();

                            timeStr = timeStr.replace("%3A", ":").replace("%20", "");

                            //  Correction : correspondance FR → EN
                            switch (dayStr) {
                                case "LUNDI": dayStr = "MONDAY"; break;
                                case "MARDI": dayStr = "TUESDAY"; break;
                                case "MERCREDI": dayStr = "WEDNESDAY"; break;
                                case "JEUDI": dayStr = "THURSDAY"; break;
                                case "VENDREDI": dayStr = "FRIDAY"; break;
                                case "SAMEDI": dayStr = "SATURDAY"; break;
                                case "DIMANCHE": dayStr = "SUNDAY"; break;
                            }

                            DayOfWeek day = DayOfWeek.valueOf(dayStr);

                            LocalTime time;
                            try {
                                time = LocalTime.parse(timeStr);
                            } catch (Exception ex) {
                                time = LocalTime.parse(timeStr + ":00");
                            }

                            builder.availableAt(time);
                            System.out.println("Filtre: ouvert le " + day + " à " + time);
                        } else {
                            String errorJson = "{\"error\": \"Format openAt invalide : " + raw + " (attendu JOUR-HH:mm)\"}";
                            exchange.getResponseHeaders().set("Content-Type", "application/json");
                            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                            exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                            exchange.getResponseBody().write(errorJson.getBytes());
                            exchange.close();
                            return;
                        }
                    } catch (Exception e) {
                        String errorJson = "{\"error\": \"Format invalide pour openAt : " + raw + " (attendu JOUR-HH:mm)\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        exchange.sendResponseHeaders(400, errorJson.getBytes().length);
                        exchange.getResponseBody().write(errorJson.getBytes());
                        exchange.close();
                        return;
                    }
                }


                // Application des filtres
                FilterCriteria criteria = builder.build();
                System.out.println("🔍 CRITÈRES REÇUS : ");
                System.out.println("name = " + criteria.getNameContains());
                System.out.println("cuisines = " + criteria.getCuisines());
                System.out.println("types = " + criteria.getEstablishments());
                System.out.println("priceMin = " + criteria.getPriceMin());
                System.out.println("priceMax = " + criteria.getPriceMax());
                System.out.println("openNow = " + criteria.getOpenNow());
                System.out.println("openAt = " + criteria.getOpenAt());
                restaurants = restaurantService.filter(criteria);
            }

            // --- Enums ---
            else if (path.equals("/restaurants/enums/cuisines")) {
                String response = mapper.writeValueAsString(CuisineType.values());
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
                return;
            } else if (path.equals("/restaurants/enums/types")) {
                String response = mapper.writeValueAsString(establishmentType.values());
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

        // Réponse JSON
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
