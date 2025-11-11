package Api.Customer;

import Order.OrderManager;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.RestaurantExist;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import Restaurant.*;


public class CustomerHandler implements HttpHandler {
    private OrderManager orderManager;
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
                    .cuisineType(List.of(CuisineType.ITALIENNE))
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
            } else if (path.equals("/restaurants/filter")) {
                // (on gérera plus tard)
            } else {
                statusCode = 404;
            }
        } else {
            statusCode = 405; // méthode non autorisée
        }


        exchange.close();
    }



}