package Api.Order;

import Account.AccountUser;
import Api.Utils.ApiRegistry;
import Api.Utils.Requester;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
import Order.Order;
import Order.OrderManager;
import Order.OrderDeliveryPoint;
import Order.OrderState;
import Order.ChooseScheduleResultDTO;
import Dish.DishNotFound;
import Dish.DishAddedDTO;
import Payment.PaymentDoneDTO;
import Payment.PaymentMethod;
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
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

public class OrderHandler implements HttpHandler{
    private OrderManager orderManager;
    private final ObjectMapper mapper = new ObjectMapper();
    public OrderHandler(OrderManager orderManager){
        this.orderManager=orderManager;
        ApiRegistry.addRoute("POST", "/api/order/", this::createOrder);
        ApiRegistry.addRoute("DELETE", "/api/order/{orderId}/", this::cancelOrder);
        ApiRegistry.addRoute("POST","/api/order/{orderId}/dish/{dishId}/",this::addItem);
        ApiRegistry.addRoute("DELETE","/api/order/{orderId}/dish/{dishId}/",this::removeItem);
        ApiRegistry.addRoute("POST","/api/order/{orderId}/pay/{paymentMethod}/",this::finishOrder);
        ApiRegistry.addRoute("POST","/api/order/{orderId}/schedule/{scheduleId}/",this::chooseSchedule);
        ApiRegistry.addRoute("POST","/api/order/{orderId}/delivery/{location}/",this::chooseDeliveryLocation);
    }

    private void createOrder(HttpExchange exchange, String... param) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> data = mapper.readValue(body, Map.class);
            Order order=orderManager.createOrder((Integer) data.get("clientId"), (String)data.get("restaurantId"));
            if(order==null){
                Sender.sendResponse(exchange,400,"Param not found");
            }
            Sender.sendResponse(exchange,200,order.getId()+"");
        } catch (Exception e) {
            e.printStackTrace();
            Sender.sendResponse(exchange, 400, "Invalid request");
        }
    }


    private void cancelOrder(HttpExchange exchange, String... param) throws IOException {
        try {
            int orderId = Integer.parseInt(param[0]);
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

    private void addItem(HttpExchange exchange,String... param) throws IOException {
        try{
            int orderId=Integer.parseInt(param[0]);
            int dishId=Integer.parseInt(param[1]);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            boolean completed = orderManager.addItem(orderId,dishId,(Integer)mapper.readValue(body,Map.class).get("quantity"));
            if(completed){
                Order currentOrder = Requester.getOrderById(orderId);
                DishAddedDTO orderState = new DishAddedDTO(currentOrder.getId(),currentOrder.getDishes(),currentOrder.getPrice());
                Sender.sendResponse(exchange,200, mapper.writeValueAsString(orderState));
            }
            else{
                Sender.sendResponse(exchange,400,"Dish not existing");
            }
        }
        catch(DishNotFound d){
            Sender.sendResponse(exchange,402,"Dish Not Found");
        }
        catch(Exception e){
            System.out.println(e);
            Sender.sendResponse(exchange,401,"Erreur");
        }
    }


    private void removeItem(HttpExchange exchange, String... param) throws IOException {
        try{
            int orderId=Integer.parseInt(param[0]);
            int dishId=Integer.parseInt(param[1]);
            boolean completed = orderManager.removeItem(orderId,dishId);
            if(completed) {
                Order currentOrder = Requester.getOrderById(orderId);
                DishAddedDTO orderState = new DishAddedDTO(currentOrder.getId(),currentOrder.getDishes(),currentOrder.getPrice());
                Sender.sendResponse(exchange,200, mapper.writeValueAsString(orderState));
            }
            else{
                Sender.sendResponse(exchange,400,"Dish not existing");
            }
        }
        catch(Exception e){
            System.out.println(e);
            Sender.sendResponse(exchange,401,"Erreur");
        }
    }
    private void finishOrder(HttpExchange exchange,String... param) throws IOException {
        try{
            System.out.println("finish reçu");
            int orderId=Integer.parseInt(param[0]);
            PaymentMethod paymentMethod = PaymentMethod.valueOf(param[1].toUpperCase());
            OrderState state = orderManager.finishOrder(orderId,paymentMethod);
            PaymentDoneDTO paymentDoneDTO = new PaymentDoneDTO(orderId,state);
            if(state==OrderState.PAID){
                Sender.sendResponse(exchange,200,mapper.writeValueAsString(paymentDoneDTO));
            }
            else{
                Sender.sendResponse(exchange,401,mapper.writeValueAsString(paymentDoneDTO));
            }
        } catch (IOException e) {
            System.out.println(e);
            Sender.sendResponse(exchange,400,"erreur du paiement");
        }
    }

    private void chooseSchedule(HttpExchange exchange,String ...params){
        try{
            System.out.println("chooseScheduleAppelé");
            int orderId = Integer.parseInt(params[0]);
            String scheduleId = params[1];
            OrderManager.chooseScheduleResult result = orderManager.chooseSchedule(orderId,scheduleId);
            if(result == OrderManager.chooseScheduleResult.SUCCESS){
                ChooseScheduleResultDTO resultDTO = new ChooseScheduleResultDTO(orderId,scheduleId,result);
                Sender.sendResponse(exchange,200,mapper.writeValueAsString(resultDTO));
                return;
            }
            if(result == OrderManager.chooseScheduleResult.ORDER_NOT_FOUND){
                Sender.sendResponse(exchange,401,"Order not found");
                return;
            }
            if(result== OrderManager.chooseScheduleResult.SCHEDULE_NOT_FOUND){
                Sender.sendResponse(exchange,401,"Schedule not found");
            }
            if(result== OrderManager.chooseScheduleResult.ERROR){
                Sender.sendResponse(exchange,401,"Erreur");
            }
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void chooseDeliveryLocation(HttpExchange exchange,String... params) throws IOException {
        try{
            int orderId = Integer.parseInt(params[0]);
            OrderDeliveryPoint deliveryLocation = OrderDeliveryPoint.valueOf(params[1].toUpperCase());
            OrderManager.chooseLocationResult result = orderManager.chooseDeliveryLocation(orderId,deliveryLocation);
            if(result == OrderManager.chooseLocationResult.ORDER_NOT_FOUND){
                Sender.sendResponse(exchange,400,"Commande pas trouvée");
                return;
            }
            if(result == OrderManager.chooseLocationResult.SUCCESS){
                Sender.sendResponse(exchange,200,"Livraison changée avec succès");
            }
        } catch (IOException e) {
            Sender.sendResponse(exchange,500,"Erreur serveur");
            throw new RuntimeException(e);
        }
    }

    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            Sender.sendResponse(exchange, 204, "");
            return;
        }
        for (RouteInfo route : ApiRegistry.getRoutes()) {
            if (!route.getMethod().equalsIgnoreCase(method)) continue;

            Matcher matcher = route.getPathMatcher(path);
            if (matcher.matches()) {
                String[] params = new String[matcher.groupCount()];
                for (int i = 0; i < matcher.groupCount(); i++) {
                    params[i] = matcher.group(i + 1);
                }
                route.getHandler().handle(exchange, params);
                return;
            }
        }
    }


}
