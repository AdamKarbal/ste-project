package Dish;

import java.util.List;
import Dish.suggest.DishSuggestionEngine;
import Dish.suggest.KeywordSuggester;
import Dish.suggest.Suggestion;

import java.util.ArrayList;
import java.util.List;
public class DishFactory {

    private DishFactory() {
    }

    /**
     * Crée un plat (Dish) avec validation des données.
     */
    public static Dish createDish(
            double price,
            String name,
            String description,
            DishCategory category,
            DishType type,
            List<DishTag> dishTags
    ) {
        validate(price, name, category, type);
        return new Dish(price, name, description, category, type, dishTags);
    }

    /**
     * Surcharge de création d’un plat sans tags.
     */
    public static Dish createDish(
            double price,
            String name,
            String description,
            DishCategory category,
            DishType type
    ) {
        return createDish(price, name, description, category, type, List.of());
    }

    /**
     * Méthode privée pour centraliser les validations.
     */
    private static void validate(double price, String name, DishCategory category, DishType type) {
        if (price <= 0) {
            throw new IllegalArgumentException("Le prix du plat doit être supérieur à 0.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du plat ne peut pas être vide.");
        }
        if (category == null) {
            throw new IllegalArgumentException("La catégorie du plat est obligatoire.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Le type du plat est obligatoire.");
        }
    }

}
