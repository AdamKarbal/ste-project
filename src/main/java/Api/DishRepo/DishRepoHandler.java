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
    }
    private void getDish(HttpExchange exchange,String dishId) throws IOException {
        System.out.println("Get reçu");
        int id = Integer.parseInt(URLDecoder.decode(dishId, StandardCharsets.UTF_8));
        Optional<Dish> optionalDish = repo.findById(id);

        if (optionalDish.isEmpty()) {
            Sender.sendResponse(exchange, 404, "{\"error\":\"Dish not found\"}");
            return;
        }
        Dish dish=optionalDish.get();
        String response = mapper.writeValueAsString(dish);

        sendResponse(exchange, 200, response);
    }

    private void createDish(HttpExchange exchange,String id) throws IOException {
        System.out.println("POST reçu");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String,Object> attributes=mapper.readValue(body,Map.class);
        double price = (double)attributes.get("price");
        String name = (String) attributes.get("name");
        String description = (String) attributes.get("description");
        DishCategory category = DishCategory.valueOf(((String) attributes.get("category")).toUpperCase());
        DishType type = DishType.valueOf(((String) attributes.get("type")).toUpperCase());
        Dish dish= repo.save(DishFactory.createDish(price,name,description,category,type));
        if(dish==null){
            Sender.sendResponse(exchange,400,"Param manquant");
        }
        Sender.sendResponse(exchange,200,dish.getId()+"");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,OPTIONS, DELETE,POST");
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
