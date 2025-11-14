package Api.Order;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
import Order.Order;
import Order.OrderManager;
import Restaurant.Restaurant;
import Restaurant.Enum.establishmentType;
import Restaurant.Enum.CuisineType;
import Restaurant.RestaurantSchedule;
import com.fasterxml.jackson.core.JsonProcessingException;
import Dish.Dish;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

public class OrderHandler implements HttpHandler{
    private OrderManager orderManager;
    private final ObjectMapper mapper = new ObjectMapper();
    public OrderHandler(OrderManager orderManager){
        this.orderManager=orderManager;
        ApiRegistry.addRoute("POST", "/api/order/", this::createOrder);
        ApiRegistry.addRoute("DELETE", "/api/order/{orderId}", this::cancelOrder);
    }

    private void createOrder(HttpExchange exchange, String param) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> data = mapper.readValue(body, Map.class);
            String restaurantName = (String) data.get("restaurantName");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8002/api/restaurantrepo/" + restaurantName))
                    .GET()
                    .build();

            CompletableFuture<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body);
            CompletableFuture<Restaurant> restaurant =response.thenApply(
                    (corps) ->{
                        try{
                            Map<String,Object> attributes = mapper.readValue(corps,Map.class);
                            String name = (String) attributes.get("name");
                            ArrayList<Dish> dishes = (ArrayList<Dish>) attributes.get("dishes");
                            ArrayList<RestaurantSchedule> schedules = (ArrayList<RestaurantSchedule>) attributes.get("restaurantSchedules");
                            establishmentType type = establishmentType.valueOf((String) attributes.get("establishmentType"));
                            ArrayList<CuisineType> cuisineType = (ArrayList<CuisineType>) attributes.get("cuisineTypes");
                            return new Restaurant.Builder().name(name).dishes(dishes).restaurantSchedules(schedules).cuisineType(cuisineType).establishmentType(type).build();
                        }
                        catch(JsonProcessingException e){
                            System.out.println(e);
                            return null;
                        }
                    }
            );
            restaurant.thenAccept(resto->{
                Order order = orderManager.createOrder(null,resto);
                try {
                    System.out.println("Commande créee");
                    Sender.sendResponse(exchange,200,order.getId()+"");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            restaurant.exceptionally(e->{
                System.err.println("Erreur: " + e.getMessage());
                try {
                    Sender.sendResponse(exchange, 500, "Erreur interne");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            });

        } catch (Exception e) {
            Sender.sendResponse(exchange, 400, "Invalid request");
        }
    }


    private void cancelOrder(HttpExchange exchange, String param) throws IOException {
        try {
            int orderId = Integer.parseInt(param);
            Order order = orderManager.cancelOrder(orderId);

            if (order != null) {
                Sender.sendResponse(exchange, 200, "Commande " + order.getId() + " annulée");
            } else {
                Sender.sendResponse(exchange, 404, "Commande non trouvée");
            }
        } catch (NumberFormatException e) {
            Sender.sendResponse(exchange, 400, "ID invalide");
        }
    }


    private void finishOrder(HttpExchange exchange, String param) throws IOException {

    }


    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        for (RouteInfo route : ApiRegistry.getRoutes()) {
            if (route.matches(requestMethod, requestPath)) {
                Matcher matcher = route.getPathMatcher(requestPath);
                String param = "";
                if (matcher.find() && matcher.groupCount()>0) {
                    param = matcher.group(1);
                }

                route.getHandler().handle(exchange, param);
                return;
            }
        }
    }


}
