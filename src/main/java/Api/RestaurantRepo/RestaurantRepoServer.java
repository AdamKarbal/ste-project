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
        List<Restaurant> seedData = setUp();
        RestaurantRepo repo = new RestaurantRepo(seedData);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nArrêt serveur : vidage json");
            repo.clearFile();
        }));
        server.createContext(path, new RestaurantRepoHandler(repo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
        return server;
    }

    private static List<Restaurant> setUp() {
        List<Restaurant> restaurants = new ArrayList<>();
        Restaurant r1 = new Restaurant.Builder()
                .name("Chez Mario")
                .establishmentType(establishmentType.RESTAURANT)
                .cuisineType(List.of(CuisineType.ITALIENNE))
                .dishes(List.of(
                        new Dish(12.5, "Pizza Margherita", "Tomate, Mozza, Basilic", DishCategory.MAIN_COURSE, DishType.PIZZA, new ArrayList<>(List.of(DishTag.VEGETARIAN))),
                        new Dish(14.0, "Pasta Carbonara", "Guanciale, Pecorino, Oeuf", DishCategory.MAIN_COURSE, DishType.PASTA, new ArrayList<>()),
                        new Dish(15.0, "Lasagne Bolognaise", "Viande hachée, Sauce tomate, Béchamel", DishCategory.MAIN_COURSE, DishType.PASTA, new ArrayList<>()),
                        new Dish(6.0, "Tiramisu", "Café, Mascarpone, Biscuit", DishCategory.DESSERT, DishType.CAKE, new ArrayList<>(List.of(DishTag.VEGETARIAN))),
                        new Dish(5.5, "Panna Cotta", "Coulis fruits rouges", DishCategory.DESSERT, DishType.OTHER, new ArrayList<>(List.of(DishTag.GLUTEN_FREE)))
                ))
                .restaurantSchedules(generateSchedules())
                .build();
        r1.getDishes().get(0).setId(1); r1.getDishes().get(1).setId(2); r1.getDishes().get(2).setId(3); r1.getDishes().get(3).setId(4); r1.getDishes().get(4).setId(5);
        Restaurant r2 = new Restaurant.Builder()
                .name("Burger King")
                .establishmentType(establishmentType.FAST_FOOD)
                .cuisineType(List.of(CuisineType.AMERICAINE))
                .dishes(List.of(
                        new Dish(11.5, "Classic Burger", "Boeuf, Cheddar, Salade, Tomate", DishCategory.MAIN_COURSE, DishType.BURGER, new ArrayList<>()),
                        new Dish(13.0, "Double Cheese", "Double Boeuf, Double Cheddar", DishCategory.MAIN_COURSE, DishType.BURGER, new ArrayList<>()),
                        new Dish(4.0, "Frites Maison", "Portion moyenne", DishCategory.STARTER, DishType.OTHER, new ArrayList<>(List.of(DishTag.VEGAN))),
                        new Dish(5.0, "Milkshake Vanille", "Glace vanille, Lait", DishCategory.DRINK, DishType.ICE_CREAM, new ArrayList<>(List.of(DishTag.CONTAINS_MILK))),
                        new Dish(10.0, "Caesar Salad", "Poulet, Parmesan, Croutons", DishCategory.MAIN_COURSE, DishType.SALAD, new ArrayList<>())
                ))
                .restaurantSchedules(generateSchedules())
                .build();
        r2.getDishes().get(0).setId(6); r2.getDishes().get(1).setId(7); r2.getDishes().get(2).setId(8); r2.getDishes().get(3).setId(9); r2.getDishes().get(4).setId(10);
        Restaurant r3 = new Restaurant.Builder()
                .name("Sushi Zen")
                .establishmentType(establishmentType.SUSHI_BAR)
                .cuisineType(List.of(CuisineType.JAPONAISE))
                .dishes(List.of(
                        new Dish(18.0, "Sushi Mix", "12 pièces (Saumon, Thon)", DishCategory.MAIN_COURSE, DishType.SEAFOOD, new ArrayList<>(List.of(DishTag.GLUTEN_FREE))),
                        new Dish(6.0, "Maki Saumon", "6 pièces", DishCategory.MAIN_COURSE, DishType.SEAFOOD, new ArrayList<>(List.of(DishTag.GLUTEN_FREE))),
                        new Dish(4.5, "Soupe Miso", "Tofu, Algues", DishCategory.STARTER, DishType.SOUP, new ArrayList<>(List.of(DishTag.VEGAN))),
                        new Dish(14.0, "Ramen Poulet", "Bouillon, Nouilles, Oeuf", DishCategory.MAIN_COURSE, DishType.SOUP, new ArrayList<>())
                ))
                .restaurantSchedules(generateSchedules())
                .build();
        r3.getDishes().get(0).setId(11); r3.getDishes().get(1).setId(12); r3.getDishes().get(2).setId(13); r3.getDishes().get(3).setId(14);
        Restaurant r4 = new Restaurant.Builder()
                .name("El Mexicano")
                .establishmentType(establishmentType.RESTAURANT)
                .cuisineType(List.of(CuisineType.MEXICAINE))
                .dishes(List.of(
                        new Dish(9.5, "Tacos Boeuf", "Viande hachée, Sauce fromagère", DishCategory.MAIN_COURSE, DishType.OTHER, new ArrayList<>(List.of(DishTag.HALAL))),
                        new Dish(8.5, "Tacos Veggie", "Haricots rouges, Poivrons", DishCategory.MAIN_COURSE, DishType.OTHER, new ArrayList<>(List.of(DishTag.VEGETARIAN, DishTag.SPICY))),
                        new Dish(7.0, "Nachos Guacamole", "Chips maïs, Avocat", DishCategory.STARTER, DishType.OTHER, new ArrayList<>(List.of(DishTag.VEGAN)))
                ))
                .restaurantSchedules(generateSchedules())
                .build();
        r4.getDishes().get(0).setId(15); r4.getDishes().get(1).setId(16); r4.getDishes().get(2).setId(17);
        Restaurant r5 = new Restaurant.Builder()
                .name("Au Bon Pain")
                .establishmentType(establishmentType.CAFE)
                .cuisineType(List.of(CuisineType.FRANCAISE))
                .dishes(List.of(
                        new Dish(5.0, "Sandwich Jambon Beurre", "Baguette, Jambon blanc", DishCategory.MAIN_COURSE, DishType.SANDWICH, new ArrayList<>()),
                        new Dish(2.0, "Croissant", "Pur beurre", DishCategory.DESSERT, DishType.OTHER, new ArrayList<>(List.of(DishTag.VEGETARIAN))),
                        new Dish(4.5, "Quiche Lorraine", "Lardons, Crème, Oeufs", DishCategory.MAIN_COURSE, DishType.OTHER, new ArrayList<>())
                ))
                .restaurantSchedules(generateSchedules())
                .build();
        r5.getDishes().get(0).setId(18); r5.getDishes().get(1).setId(19); r5.getDishes().get(2).setId(20);

        restaurants.add(r1);
        restaurants.add(r2);
        restaurants.add(r3);
        restaurants.add(r4);
        restaurants.add(r5);

        return restaurants;
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
                DishType.BURGER,
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

    private static List<RestaurantSchedule> generateSchedules() {
        List<RestaurantSchedule> schedules = new ArrayList<>();
        for (DayOfTheWeek day : DayOfTheWeek.values()) {
            LocalTime current = LocalTime.of(11, 30);
            while (current.isBefore(LocalTime.of(14, 30))) {
                int capacity = 5 + (int)(Math.random() * 26);
                schedules.add(new RestaurantSchedule(day, current, capacity));
                current = current.plusMinutes(30);
            }
            current = LocalTime.of(18, 30);
            while (current.isBefore(LocalTime.of(22, 30))) {
                int capacity = 10 + (int)(Math.random() * 21);
                schedules.add(new RestaurantSchedule(day, current, capacity));
                current = current.plusMinutes(30);
            }
        }
        return schedules;
    }
    
}