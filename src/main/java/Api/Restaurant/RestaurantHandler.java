package Api.Restaurant;

import Restaurant.*;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.*;
import Dish.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Map;

public class RestaurantHandler implements HttpHandler {
    private final RestaurantManager restaurantManager;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public RestaurantHandler(RestaurantManager restaurantManager) {
        this.restaurantManager = restaurantManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String route = path.replaceFirst("/api/restaurant", "");
        if (route.isEmpty()) route = "/";
        try {
            switch (method) {
                case "POST":
                    handlePost(exchange, route);
                    break;
                case "PUT":
                    handlePut(exchange, route);
                    break;
                case "DELETE":
                    handleDelete(exchange, route);
                    break;
                default:
                    sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(exchange, 500, "Erreur interne : " + e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange, String route) throws IOException {
        String[] parts = route.split("/");
        if (route.equals("/") || route.isEmpty()) {
            handleCreateRestaurant(exchange);
            return;
        }
        if (parts.length < 2) {
            sendErrorResponse(exchange, 400, "Nom du restaurant manquant");
            return;
        }
        String restaurantName = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        if (parts.length == 3 && parts[2].equals("dishes")) {
            try {
                Dish dish = objectMapper.readValue(readRequestBody(exchange), Dish.class);
                restaurantManager.addDish(dish, restaurantName);
                sendResponse(exchange, 201, "{\"message\": \"Plat ajouté\"}");
            } catch (DishAlreadyInDishes e) {
                sendErrorResponse(exchange, 409, e.getMessage());
            }
        }
        else if (parts.length == 3 && parts[2].equals("cuisine-types")) {
            try {
                String typeStr = objectMapper.readValue(readRequestBody(exchange), String.class);
                CuisineType cType = CuisineType.valueOf(typeStr.toUpperCase());
                restaurantManager.addCuisineType(cType, restaurantName);
                sendResponse(exchange, 201, "{\"message\": \"Type de cuisine ajouté\"}");
            } catch (CuisineTypeInList e) {
                sendErrorResponse(exchange, 409, e.getMessage());
            }
        }
        else if (parts.length == 3 && parts[2].equals("schedules")) {
            Map<String, Object> body = objectMapper.readValue(readRequestBody(exchange), Map.class);
            DayOfTheWeek day = DayOfTheWeek.valueOf((String) body.get("day"));
            LocalTime start = LocalTime.parse((String) body.get("start"));
            LocalTime end = LocalTime.parse((String) body.get("end"));
            int capacity = (int) body.get("capacity");
            restaurantManager.updateSchedule(day, start, end, capacity, restaurantName);
            sendResponse(exchange, 200, "{\"message\": \"Horaire mis à jour/ajouté\"}");
        }
        else {
            sendErrorResponse(exchange, 404, "Route POST inconnue : " + route);
        }
    }

    private void handlePut(HttpExchange exchange, String route) throws IOException {
        String[] parts = route.split("/");
        if (parts.length < 2) {
            sendErrorResponse(exchange, 400, "Nom du restaurant manquant");
            return;
        }
        String restaurantName = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        if (parts.length == 3 && parts[2].equals("establishment-type")) {
            try {
                String typeStr = objectMapper.readValue(readRequestBody(exchange), String.class);
                establishmentType eType = establishmentType.valueOf(typeStr.toUpperCase());
                restaurantManager.addEstablishmentType(eType, restaurantName);
                sendResponse(exchange, 200, "{\"message\": \"Type d'établissement défini\"}");
            } catch (EstablishmentTypeAlreadySet e) {
                sendErrorResponse(exchange, 409, e.getMessage());
            }
        }
        else if (parts.length == 4 && parts[2].equals("dishes")) {
            String oldDishName = URLDecoder.decode(parts[3], StandardCharsets.UTF_8);
            try {
                Dish newDish = objectMapper.readValue(readRequestBody(exchange), Dish.class);
                Dish oldDishDummy = new Dish();
                oldDishDummy.setName(oldDishName);
                restaurantManager.updateDish(oldDishDummy, newDish, restaurantName);
                sendResponse(exchange, 200, "{\"message\": \"Plat mis à jour\"}");
            } catch (dishNoInDishes e) {
                sendErrorResponse(exchange, 404, e.getMessage());
            }
        }
        else if (parts.length == 4 && parts[2].equals("schedules") && parts[3].equals("capacity")) {
            Map<String, Object> body = objectMapper.readValue(readRequestBody(exchange), Map.class);
            DayOfTheWeek day = DayOfTheWeek.valueOf((String) body.get("day"));
            LocalTime start = LocalTime.parse((String) body.get("start"));
            LocalTime end = LocalTime.parse((String) body.get("end"));
            int capacity = (int) body.get("capacity");
            try {
                restaurantManager.updateCapacity(day, start, end, capacity, restaurantName);
                sendResponse(exchange, 200, "{\"message\": \"Capacité mise à jour\"}");
            } catch (ScheduleNoExist e) {
                sendErrorResponse(exchange, 404, e.getMessage());
            }
        }
        else {
            sendErrorResponse(exchange, 404, "Route PUT inconnue : " + route);
        }
    }

    private void handleDelete(HttpExchange exchange, String route) throws IOException {
        String[] parts = route.split("/");
        if (parts.length < 2) {
            sendErrorResponse(exchange, 400, "Nom du restaurant manquant");
            return;
        }
        String restaurantName = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        if (parts.length == 3 && parts[2].equals("establishment-type")) {
            try {
                restaurantManager.removeEstablishmentType(restaurantName);
                sendResponse(exchange, 200, "{\"message\": \"Type d'établissement supprimé\"}");
            } catch (EstablishmentTypeNoExist e) {
                sendErrorResponse(exchange, 404, e.getMessage());
            }
        }
        else if (parts.length == 4 && parts[2].equals("cuisine-types")) {
            String typeStr = parts[3];
            try {
                CuisineType cType = CuisineType.valueOf(typeStr.toUpperCase());
                restaurantManager.removeCuisineType(cType, restaurantName);
                sendResponse(exchange, 200, "{\"message\": \"Type de cuisine supprimé\"}");
            } catch (CuisineTypeNoExist e) {
                sendErrorResponse(exchange, 404, e.getMessage());
            }
        }
        else if (parts.length == 4 && parts[2].equals("dishes")) {
            String dishName = URLDecoder.decode(parts[3], StandardCharsets.UTF_8);
            try {
                Dish dishToDelete = new Dish();
                dishToDelete.setName(dishName);
                restaurantManager.removeDish(dishToDelete, restaurantName);
                sendResponse(exchange, 200, "{\"message\": \"Plat supprimé\"}");
            } catch (dishNoInDishes e) {
                sendErrorResponse(exchange, 404, e.getMessage());
            }
        }
        else {
            sendErrorResponse(exchange, 404, "Route DELETE inconnue : " + route);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
        exchange.close();
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String errorMessage) throws IOException {
        String cleanMessage = errorMessage.replace("\"", "'");
        String errorResponse = String.format("{\"error\": \"%s\"}", cleanMessage);
        sendResponse(exchange, statusCode, errorResponse);
    }

    private void handleCreateRestaurant(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> body = objectMapper.readValue(readRequestBody(exchange), Map.class);
            String name = body.get("name");
            if (name == null || name.isEmpty()) {
                sendErrorResponse(exchange, 400, "Bad Request: 'name' est requis.");
                return;
            }
            RestaurantFactory localFactory = new RestaurantFactory();
            Restaurant newRestaurant = localFactory.createRestaurant(name);
            restaurantManager.saveRestaurant(newRestaurant);
            sendResponse(exchange, 201, "{\"message\": \"Restaurant " + name + " created\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(exchange, 500, "Erreur lors de la création : " + e.getMessage());
        }
    }
}