package Api.OrderRepo;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
import Order.Order;
import Order.OrderRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class OrderRepoHandler implements HttpHandler {
    private OrderRepo repo;
    private final ObjectMapper mapper = new ObjectMapper();
    public OrderRepoHandler(OrderRepo repo){
        this.repo=repo;
        ApiRegistry.addRoute("GET","/api/orderRepo/{orderId}/",this::getOrderById);
        ApiRegistry.addRoute("POST",   "/api/orderRepo/",          this::createOrder);
        ApiRegistry.addRoute("PUT",    "/api/orderRepo/{orderId}/", this::updateOrder);
        ApiRegistry.addRoute("DELETE", "/api/orderRepo/{orderId}/", this::deleteOrder);
        ApiRegistry.addRoute("GET","/api/orderRepo/",this::getAllOrders);
    }
    public void getOrderById(HttpExchange exchange, String... param) throws IOException {
        try{
            int id = Integer.parseInt(param[0]);
            Optional<Order> optionalOrder = repo.findById(id);
            if (optionalOrder.isEmpty()) {
                Sender.sendResponse(exchange, 404, "Order not found");
                return;
            }

            Order order = optionalOrder.get();
            String responseJson = mapper.writeValueAsString(order);
            Sender.sendResponse(exchange, 200, responseJson);
        }
        catch(Exception e){
            System.out.println(e);
            Sender.sendResponse(exchange,500,"Erreur");
        }

    }
    private void createOrder(HttpExchange exchange, String... param) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Order order = mapper.readValue(body, Order.class); // c là l'erreur
            repo.save(order);
            String json = mapper.writeValueAsString(order);
            Sender.sendResponse(exchange, 201, json);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            Sender.sendResponse(exchange, 500, "Internal server error");
        }
    }
    private void updateOrder(HttpExchange exchange, String... param) throws IOException {
        try {
            int id = Integer.parseInt(param[0]);

            Optional<Order> optionalOrder = repo.findById(id);
            if (optionalOrder.isEmpty()) {
                Sender.sendResponse(exchange, 404, "Order not found");
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Order newOrder = mapper.readValue(body, Order.class);
            newOrder.setId(id);

            repo.save(newOrder); // remplace l'ancien

            String json = mapper.writeValueAsString(newOrder);
            Sender.sendResponse(exchange, 200, json);

        } catch (Exception e) {
            Sender.sendResponse(exchange, 500, "Internal server error");
        }
    }
    private void deleteOrder(HttpExchange exchange, String... param) throws IOException {
        try {
            int id = Integer.parseInt(param[0]);

            // Ton OrderRepo a une méthode bizarrement nommée getById qui… supprime 😅
            boolean removed = repo.deleteById(id);

            if (!removed) {
                Sender.sendResponse(exchange, 404, "Order not found");
                return;
            }

            // Pas besoin de corps
            Sender.sendResponse(exchange, 204, ""); // 204 No Content

        } catch (Exception e) {
            e.printStackTrace();
            Sender.sendResponse(exchange, 500, "Internal server error");
        }
    }
    private void getAllOrders(HttpExchange exchange,String... params) throws IOException {
        try{
            List<Order> orders=repo.getAllOrders();
            if(orders==null){
                Sender.sendResponse(exchange,400,"List des orders vide");
                return;
            }
            String responseJson = mapper.writeValueAsString(orders);
            Sender.sendResponse(exchange,200,responseJson);
        } catch (IOException e) {
            Sender.sendResponse(exchange,500,"Erreur");
            throw new RuntimeException(e);
        }
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
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
                if (matcher.find() && matcher.groupCount()>0) {
                    param = matcher.group(1);
                }

                route.getHandler().handle(exchange, param);
                return;
            }
        }
    }
}
