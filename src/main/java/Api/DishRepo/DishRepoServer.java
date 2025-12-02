package Api.DishRepo;

import Api.OrderRepo.OrderRepoHandler;
import Dish.Dish;
import Dish.DishRepo;
import Dish.DishCategory;
import Dish.DishType;
import Dish.DishTag;
import Dish.DishTopping;
import Order.OrderRepo;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class DishRepoServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException{
        int port = 8004;
        String path = "/api/dishRepo/";// port dédié au Customer Service
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        List<Dish> seedDishes = setUpDishes();
        DishRepo repo = new DishRepo(seedDishes);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nArrêt du dishrepo: vidage json");
            repo.ClearFile();
        }));
        server.createContext(path, new DishRepoHandler(repo));
        server.setExecutor(null);
        server.start();

        System.out.println("✅ RestaurantRepo en ligne sur http://localhost:" + port + path);
        return server;
    }

    public static List<Dish> setUpDishes() {
        List<Dish> dishes = new ArrayList<>();
        Dish d1 = new Dish(12.5, "Pizza Margherita", "Tomate, Mozza, Basilic", DishCategory.MAIN_COURSE, DishType.PIZZA, new ArrayList<>(List.of(DishTag.VEGETARIAN)));
        d1.setId(1);
        d1.addAvailableTopping(new DishTopping("Olives", 0.5f));
        Dish d2 = new Dish(14.0, "Pasta Carbonara", "Guanciale, Pecorino, Oeuf", DishCategory.MAIN_COURSE, DishType.PASTA, new ArrayList<>());
        d2.setId(2);
        Dish d3 = new Dish(15.0, "Lasagne Bolognaise", "Viande hachée, Sauce tomate, Béchamel", DishCategory.MAIN_COURSE, DishType.PASTA, new ArrayList<>());
        d3.setId(3);
        Dish d4 = new Dish(6.0, "Tiramisu", "Café, Mascarpone, Biscuit", DishCategory.DESSERT, DishType.CAKE, new ArrayList<>(List.of(DishTag.VEGETARIAN)));
        d4.setId(4);
        Dish d5 = new Dish(5.5, "Panna Cotta", "Coulis fruits rouges", DishCategory.DESSERT, DishType.OTHER, new ArrayList<>(List.of(DishTag.GLUTEN_FREE)));
        d5.setId(5);
        Dish d6 = new Dish(11.5, "Classic Burger", "Boeuf, Cheddar, Salade, Tomate", DishCategory.MAIN_COURSE, DishType.BURGER, new ArrayList<>());
        d6.setId(6);
        d6.addAvailableTopping(new DishTopping("Bacon", 1.5f));
        Dish d7 = new Dish(13.0, "Double Cheese", "Double Boeuf, Double Cheddar", DishCategory.MAIN_COURSE, DishType.BURGER, new ArrayList<>());
        d7.setId(7);
        Dish d8 = new Dish(4.0, "Frites Maison", "Portion moyenne", DishCategory.STARTER, DishType.OTHER, new ArrayList<>(List.of(DishTag.VEGAN)));
        d8.setId(8);
        Dish d9 = new Dish(5.0, "Milkshake Vanille", "Glace vanille, Lait", DishCategory.DRINK, DishType.ICE_CREAM, new ArrayList<>(List.of(DishTag.CONTAINS_MILK)));
        d9.setId(9);
        Dish d10 = new Dish(10.0, "Caesar Salad", "Poulet, Parmesan, Croutons", DishCategory.MAIN_COURSE, DishType.SALAD, new ArrayList<>());
        d10.setId(10);
        Dish d11 = new Dish(18.0, "Sushi Mix", "12 pièces (Saumon, Thon)", DishCategory.MAIN_COURSE, DishType.SEAFOOD, new ArrayList<>(List.of(DishTag.GLUTEN_FREE)));
        d11.setId(11);
        Dish d12 = new Dish(6.0, "Maki Saumon", "6 pièces", DishCategory.MAIN_COURSE, DishType.SEAFOOD, new ArrayList<>(List.of(DishTag.GLUTEN_FREE)));
        d12.setId(12);
        Dish d13 = new Dish(4.5, "Soupe Miso", "Tofu, Algues", DishCategory.STARTER, DishType.SOUP, new ArrayList<>(List.of(DishTag.VEGAN)));
        d13.setId(13);
        Dish d14 = new Dish(14.0, "Ramen Poulet", "Bouillon, Nouilles, Oeuf", DishCategory.MAIN_COURSE, DishType.SOUP, new ArrayList<>());
        d14.setId(14);
        Dish d15 = new Dish(9.5, "Tacos Boeuf", "Viande hachée, Sauce fromagère", DishCategory.MAIN_COURSE, DishType.OTHER, new ArrayList<>(List.of(DishTag.HALAL)));
        d15.setId(15);
        Dish d16 = new Dish(8.5, "Tacos Veggie", "Haricots rouges, Poivrons", DishCategory.MAIN_COURSE, DishType.OTHER, new ArrayList<>(List.of(DishTag.VEGETARIAN, DishTag.SPICY)));
        d16.setId(16);
        Dish d17 = new Dish(7.0, "Nachos Guacamole", "Chips maïs, Avocat", DishCategory.STARTER, DishType.OTHER, new ArrayList<>(List.of(DishTag.VEGAN)));
        d17.setId(17);
        Dish d18 = new Dish(5.0, "Sandwich Jambon Beurre", "Baguette, Jambon blanc", DishCategory.MAIN_COURSE, DishType.SANDWICH, new ArrayList<>());
        d18.setId(18);
        Dish d19 = new Dish(2.0, "Croissant", "Pur beurre", DishCategory.DESSERT, DishType.OTHER, new ArrayList<>(List.of(DishTag.VEGETARIAN)));
        d19.setId(19);
        Dish d20 = new Dish(4.5, "Quiche Lorraine", "Lardons, Crème, Oeufs", DishCategory.MAIN_COURSE, DishType.OTHER, new ArrayList<>());
        d20.setId(20);
        dishes.add(d1); dishes.add(d2); dishes.add(d3); dishes.add(d4); dishes.add(d5);
        dishes.add(d6); dishes.add(d7); dishes.add(d8); dishes.add(d9); dishes.add(d10);
        dishes.add(d11); dishes.add(d12); dishes.add(d13); dishes.add(d14);
        dishes.add(d15); dishes.add(d16); dishes.add(d17);
        dishes.add(d18); dishes.add(d19); dishes.add(d20);
        return dishes;
    }
}
