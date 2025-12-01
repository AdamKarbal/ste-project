package Api.DishRepo;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
import Dish.DishRepo;
import Dish.Dish;
import Dish.DishFactory;
import Dish.DishCategory;
import Dish.DishType;
import Order.Order;
import Restaurant.Restaurant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import static Api.Utils.Sender.sendResponse;

public class DishRepoHandler implements HttpHandler {
    private DishRepo repo;
    private final ObjectMapper mapper = new ObjectMapper();
    public DishRepoHandler(DishRepo repo){
        this.repo=repo;
        ApiRegistry.addRoute("GET","/api/dishRepo/{dishId}/",this::getDish);
        ApiRegistry.addRoute("POST","/api/dishRepo/",this::createDish);
        ApiRegistry.addRoute("GET","/api/dishRepo/",this::getAllDishes);
        ApiRegistry.addRoute("PUT","/api/dishRepo/{dishId}/",this::updateDish);
    }
    private void getDish(HttpExchange exchange,String... params) throws IOException {
        try{
            int id = Integer.parseInt(URLDecoder.decode(params[0], StandardCharsets.UTF_8));
            Optional<Dish> optionalDish = repo.findById(id);

            if (optionalDish.isEmpty()) {
                Sender.sendResponse(exchange, 404, "{\"error\":\"Dish not found\"}");
                return;
            }
            Dish dish=optionalDish.get();
            String response = mapper.writeValueAsString(dish);

            Sender.sendResponse(exchange, 200, response);
        }
        catch (Exception e){
            System.out.println(e);
            Sender.sendResponse(exchange,500,"Erreur interne");
        }
    }

    private void getAllDishes(HttpExchange exchange,String... params) throws IOException{
        try{
                List<Dish> dishes = repo.getAllDishes();
                if(dishes==null){
                    sendResponse(exchange,404,"Liste des restos vide");
                    return;
                }
                String responseJson = mapper.writeValueAsString(dishes);
                sendResponse(exchange, 200, responseJson);}
        catch(Exception e){
                e.printStackTrace();
                sendResponse(exchange, 500, "Erreur interne du serveur : " + e.getMessage());
        }

    }

    private void createDish(HttpExchange exchange,String... id) throws IOException {
        try{
            System.out.println(">> createDish appelé");
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            //Dish dish = mapper.readValue(body,Dish.class);
            Map<String,Object> attributes=mapper.readValue(body,Map.class);
            Number priceNumber = (Number) attributes.get("price");
            double price = priceNumber.doubleValue();
            String name = (String) attributes.get("name");
            String description = (String) attributes.get("description");
            DishCategory category = DishCategory.valueOf(((String) attributes.get("category")).toUpperCase());
            DishType type = DishType.valueOf(((String) attributes.get("type")).toUpperCase());
            Dish dish = repo.save(DishFactory.createDish(price,name,description,category,type));
            String json = mapper.writeValueAsString(dish);
            Sender.sendResponse(exchange,200,json);
        } catch (Exception e) {
            System.out.println(e);
            Sender.sendResponse(exchange,400,"Param manquant");
        }
    }

    private void updateDish(HttpExchange exchange, String... params) throws IOException {
        try {
            int id = Integer.parseInt(URLDecoder.decode(params[0], StandardCharsets.UTF_8));
            Optional<Dish> optionalDish = repo.findById(id);

            if (optionalDish.isEmpty()) {
                Sender.sendResponse(exchange, 404, "{\"error\":\"Dish not found\"}");
                return;
            }

            Dish dish = optionalDish.get();

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> attributes = mapper.readValue(body, Map.class);

            Number priceNumber = (Number) attributes.get("price");
            String name = (String) attributes.get("name");
            String description = (String) attributes.get("description");
            String categoryStr = (String) attributes.get("category");
            String typeStr = (String) attributes.get("type");

            if (priceNumber == null || name == null || description == null
                    || categoryStr == null || typeStr == null) {
                Sender.sendResponse(exchange, 400, "{\"error\":\"Missing parameter\"}");
                return;
            }

            double price = priceNumber.doubleValue();
            DishCategory category = DishCategory.valueOf(categoryStr.toUpperCase());
            DishType type = DishType.valueOf(typeStr.toUpperCase());

            dish.setPrice((float) price);
            dish.setName(name);
            dish.setDescription(description);
            dish.setCategory(category);
            dish.setType(type);

            repo.save(dish);

            String json = mapper.writeValueAsString(dish);
            Sender.sendResponse(exchange, 200, json);

        } catch (IllegalArgumentException e) {
            Sender.sendResponse(exchange, 400, "{\"error\":\"Invalid category or type\"}");
        } catch (Exception e) {
            Sender.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,OPTIONS, DELETE,POST");
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
