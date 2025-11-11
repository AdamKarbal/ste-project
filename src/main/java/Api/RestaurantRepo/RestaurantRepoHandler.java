package Api.RestaurantRepo;

import Api.Utils.ApiRegistry;
import Api.Utils.RouteInfo;
import Restaurant.RestaurantRepo;
import Restaurant.Restaurant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;

import static Api.Utils.Sender.sendResponse;

public class RestaurantRepoHandler implements HttpHandler {
    private RestaurantRepo repo;
    private final ObjectMapper mapper = new ObjectMapper();
    public RestaurantRepoHandler(RestaurantRepo repo){
        this.repo=repo;
        ApiRegistry.addRoute("GET", "/api/restaurantrepo/{restaurantName}", this::getRestaurant);
    }
    public void getRestaurant(HttpExchange exchange,String param1) throws IOException {
        // 2️⃣ Récupère le paramètre 'name' depuis la query string
        System.out.println("Get reçu");
        String name = URLDecoder.decode(param1, StandardCharsets.UTF_8);
        Restaurant restaurantOpt = repo.getRestaurantByName(name);

        if (restaurantOpt==null) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant not found\"}");
            return;
        }
        String response = mapper.writeValueAsString(restaurantOpt);

        sendResponse(exchange, 200, response);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET");
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
