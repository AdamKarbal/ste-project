package Api.RestaurantRepo;

import Api.Customer.CustomerHandler;
import Dish.Dish;
import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.establishmentType;
import Restaurant.Restaurant;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantSchedule;
import com.sun.net.httpserver.HttpServer;
import Dish.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepoServer {

    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException {
        int port = 8002;
        String path = "/api/restaurantrepo/";
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        //RestaurantRepo repo = new RestaurantRepo(setUpTest());
        RestaurantRepo repo = new RestaurantRepo(new ArrayList<>());
        server.createContext(path, new RestaurantRepoHandler(repo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
        return server;
    }
    public static List<Restaurant> setUpTest() {

        List<Restaurant> restaurants = new ArrayList<>();
        List<RestaurantSchedule> schedules = List.of(
                new RestaurantSchedule(DayOfTheWeek.MONDAY,
                        LocalTime.of(11, 30),
                        50),

                new RestaurantSchedule(DayOfTheWeek.MONDAY,
                        LocalTime.of(18, 0),
                        70),

                new RestaurantSchedule(DayOfTheWeek.TUESDAY,
                        LocalTime.of(11, 30),
                        50),

                new RestaurantSchedule(DayOfTheWeek.TUESDAY,
                        LocalTime.of(18, 0),
                        70),

                new RestaurantSchedule(DayOfTheWeek.WEDNESDAY,
                        LocalTime.of(11, 30),
                        50),

                new RestaurantSchedule(DayOfTheWeek.WEDNESDAY,
                        LocalTime.of(18, 0),
                        70),

                new RestaurantSchedule(DayOfTheWeek.THURSDAY,
                        LocalTime.of(11, 30),
                        50),

                new RestaurantSchedule(DayOfTheWeek.THURSDAY,
                        LocalTime.of(18, 0),
                        70),

                new RestaurantSchedule(DayOfTheWeek.FRIDAY,
                        LocalTime.of(11, 30),
                        60),

                new RestaurantSchedule(DayOfTheWeek.FRIDAY,
                        LocalTime.of(19, 0),
                        80),

                new RestaurantSchedule(DayOfTheWeek.SATURDAY,
                        LocalTime.of(12, 0),
                        100),

                new RestaurantSchedule(DayOfTheWeek.SUNDAY,
                        LocalTime.of(12, 0),
                        80),
                new RestaurantSchedule(DayOfTheWeek.SUNDAY,
                        LocalTime.of(20, 0),
                        80)
        );
        Dish tacosClassic = new Dish(
                7.5,
                "Tacos Classique",
                "Tacos avec poulet, frites, sauce gruyère.",
                DishCategory.MAIN_COURSE,
                DishType.OTHER,
                new ArrayList<>(List.of(DishTag.HALAL))
        );
        tacosClassic.addAvailableTopping(new DishTopping("Fromage supplément", 1.0f));
        tacosClassic.addAvailableTopping(new DishTopping("Sauce algérienne", 0.5f));

        Dish tacosVegan = new Dish(
                8.0,
                "Tacos Vegan",
                "Tacos garni de légumes grillés et sauce vegan.",
                DishCategory.MAIN_COURSE,
                DishType.OTHER,
                new ArrayList<>(List.of(DishTag.VEGAN, DishTag.GLUTEN_FREE))
        );
        tacosVegan.addAvailableTopping(new DishTopping("Falafel", 1.5f));

        Restaurant r1 = new Restaurant.Builder()
                .name("Tacos")
                .establishmentType(establishmentType.RESTAURANT)
                .cuisineType(List.of(CuisineType.MEXICAINE))
                .dishes(List.of(tacosClassic, tacosVegan))
                .restaurantSchedules(schedules)
                .build();


        Dish margarita = new Dish(
                9.0,
                "Pizza Margherita",
                "Tomates, mozzarella, basilic frais.",
                DishCategory.MAIN_COURSE,
                DishType.PIZZA,
                new ArrayList<>(List.of(DishTag.VEGETARIAN))
        );
        margarita.addAvailableTopping(new DishTopping("Olives", 0.5f));
        margarita.addAvailableTopping(new DishTopping("Piments", 0.7f));

        Dish tartufo = new Dish(
                14.5,
                "Pizza Tartufo",
                "Crème de truffe, champignons, mozzarella.",
                DishCategory.MAIN_COURSE,
                DishType.PIZZA,
                new ArrayList<>(List.of(DishTag.ORGANIC))
        );

        Dish tiramisu = new Dish(
                5.5,
                "Tiramisu",
                "Dessert traditionnel italien.",
                DishCategory.DESSERT,
                DishType.CAKE,
                new ArrayList<>(List.of(DishTag.CONTAINS_MILK))
        );

        Restaurant r2 = new Restaurant.Builder()
                .name("Pizza Express")
                .establishmentType(establishmentType.RESTAURANT)
                .cuisineType(List.of(CuisineType.ITALIENNE))
                .dishes(List.of(margarita, tartufo, tiramisu))
                .restaurantSchedules(new ArrayList<>())
                .build();


        Dish salad = new Dish(
                6.0,
                "Salade Fraîcheur",
                "Salade verte, tomates, maïs, vinaigrette maison.",
                DishCategory.STARTER,
                DishType.SALAD,
                new ArrayList<>(List.of(DishTag.VEGAN, DishTag.GLUTEN_FREE))
        );

        Dish burger = new Dish(
                9.0,
                "Burger Classique",
                "Steak, cheddar, oignons, sauce maison.",
                DishCategory.MAIN_COURSE,
                DishType.SANDWICH,
                new ArrayList<>(List.of(DishTag.HOMEMADE))
        );
        burger.addAvailableTopping(new DishTopping("Bacon", 1.0f));
        burger.addAvailableTopping(new DishTopping("Cheddar supplément", 0.8f));

        Dish mocha = new Dish(
                3.9,
                "Café Mocha",
                "Café gourmand chocolat.",
                DishCategory.DRINK,
                DishType.OTHER,
                new ArrayList<>(List.of(DishTag.ORGANIC))
        );

        Restaurant r3 = new Restaurant.Builder()
                .name("Cafeteria du Campus")
                .establishmentType(establishmentType.FAST_FOOD)
                .cuisineType(List.of(CuisineType.AMERICAINE))
                .dishes(List.of(salad, burger, mocha))
                .restaurantSchedules(new ArrayList<>())
                .build();


        // Retour final
        restaurants.add(r1);
        restaurants.add(r2);
        restaurants.add(r3);

        return restaurants;
    }
}