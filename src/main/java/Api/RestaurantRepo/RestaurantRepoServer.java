package Api.RestaurantRepo;

import Api.Customer.CustomerHandler;
import Dish.Dish;
import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepoServer {
    public static void main(String[] args) throws IOException {
        int port = 8002;
        String path = "/api/restaurantrepo/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        RestaurantRepo repo = new RestaurantRepo(new ArrayList<>());
        repo.addRestaurant(setUpTest());
        server.createContext(path, new RestaurantRepoHandler(repo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
    }
    public static Restaurant setUpTest(){
        Dish dishSansGlutenEtVegan = new Dish(0f, "Dish SG+V", null, null, null,
                new ArrayList<>(List.of(DishTag.GLUTEN_FREE, DishTag.VEGAN)));

        Dish dishVegan = new Dish(0f, "Dish Vegan",null, null, null,
                new ArrayList<>(List.of(DishTag.VEGAN)));

        Dish dishSansGluten = new Dish(0f, "Dish SG", null, null, null,
                new ArrayList<>(List.of(DishTag.GLUTEN_FREE)));

        return new Restaurant.Builder()
                .name("Tacos")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(new ArrayList<>())     // always
                .dishes(List.of(dishSansGluten, dishVegan,dishSansGlutenEtVegan))
                .cuisineType(List.of(CuisineType.ITALIENNE))
                .build();
    }
}
