package Dish.suggest;

import Account.AccountManager;
import Dish.*;
import Restaurant.RestaurantFactory;
import Restaurant.RestaurantManager;
import Restaurant.RestaurantRepo;
import Restaurant.RestaurantService;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) throws Exception {
        // 1) Préparer un restaurant “vide”
        RestaurantRepo repo = new RestaurantRepo(new ArrayList<>());
        RestaurantService restaurantService = new RestaurantService(repo);
        RestaurantFactory restaurantFactory = new RestaurantFactory(restaurantService);
        AccountManager Olivier = new AccountManager("Olivier");
        Olivier.createRestaurant("Chez test", restaurantFactory);
        RestaurantManager manager =  Olivier.getRestaurantManager();


        // 2) Prévisualiser les suggestions à partir du nom
        Suggestion pref = manager.previewSuggestion("Pizza Margherita");
        System.out.println("=== Preview (Suggestion) ===");
        System.out.println(pref); // utilise Suggestion.toString()

        // 3) L'owner modifie 2-3 champs → on construit le DishInput
        DishInput in = new DishInput();
        in.name = "Pizza Margherita";          // déclenche la suggestion PIZZA
        in.price = 12.0;                       // l’owner augmente le prix (prime sur suggestedPrice=10.0)
        in.toppings = List.of(new DishTopping("Olives", 0.8f)); // l’owner ajoute un topping

        // 4) Créer + ajouter le plat via le manager (qui délègue à la factory)
        Dish created = manager.createDish(in);

        // 5) Vérifs visuelles
        System.out.println("\n=== Dish créé ===");
        System.out.println("name=" + created.getName());
        System.out.println("type=" + created.getType());          // attendu: PIZZA (depuis la suggestion)
        System.out.println("price=" + created.getPrice());        // attendu: 12.0 (user > suggestion)
        System.out.println("toppings=" + created.getSelectedToppings());  // attendu (merge): Mozzarella, Basilic, Olives

        System.out.println("\n=== Dishes dans le restaurant ===");
        System.out.println(manager.getRestaurant().getDishes().size()); // attendu: 1
    }
}
