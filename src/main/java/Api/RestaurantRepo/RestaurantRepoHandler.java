package Api.RestaurantRepo;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Api.Utils.Sender;
import Restaurant.RestaurantRepo;
import Restaurant.Restaurant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static Api.Utils.Sender.sendResponse;

public class RestaurantRepoHandler implements HttpHandler {
    private RestaurantRepo repo;
    private final ObjectMapper mapper = new ObjectMapper();
    public RestaurantRepoHandler(RestaurantRepo repo){
        this.repo=repo;
        ApiRegistry.addRoute("GET", "/api/restaurantrepo/{restaurantName}/", this::getRestaurant);
        ApiRegistry.addRoute("GET","api/restaurantrepo/",this::getAllRestaurants);
        //faudra ajouter le POST
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

    public void getAllRestaurants(HttpExchange exchange,String... param) throws IOException{
        try{List<Restaurant> restaurants = repo.getRestaurants();
        if(restaurants==null){
            sendResponse(exchange,404,"Liste des restos vide");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        String responseJson = mapper.writeValueAsString(restaurants);
        sendResponse(exchange, 200, responseJson);}
        catch(Exception e){
            e.printStackTrace();
            sendResponse(exchange, 500, "Erreur interne du serveur : " + e.getMessage());
        }

    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET");
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
