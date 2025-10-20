package Dish;

import Dish.suggest.Suggestion;
import java.util.ArrayList;
import java.util.List;

/** Fabrique un Dish final à partir de l'entrée utilisateur (DishInput) et d'une Suggestion. */
public class DishFactory {

    /** Règle : la saisie utilisateur prime champ par champ. Merge des tags & toppings. */
    public Dish create(DishInput in, Suggestion s) {
        if (in == null || in.name == null || in.name.trim().isEmpty())
            throw new IllegalArgumentException("Dish name is required");
        String name = in.name.trim();

        String description    = first(in.description, s != null ? s.description : null);
        DishCategory category = first(in.category,   s != null ? s.category   : null);
        DishType type         = first(in.type,       s != null ? s.type       : null);

        Double price = first(in.price, s != null ? s.suggestedPrice : null);
        if (price == null) price = 0.0;

        // Merge TAGS (suggestion ∪ saisie)
        List<DishTag> tags = new ArrayList<>();
        if (s != null && s.tags != null) tags.addAll(s.tags);
        if (in.tags != null) {
            tags.removeAll(in.tags);
            tags.addAll(in.tags);
        }

        // Merge TOPPINGS (sans doublons, grâce à equals/hashCode de DishTopping)
        List<DishTopping> tops = new ArrayList<>();
        if (s != null && s.toppings != null) tops.addAll(s.toppings);
        if (in.toppings != null) {
            for (DishTopping t : in.toppings) if (!tops.contains(t)) tops.add(t);
        }

        Dish dish = new Dish(price, name, description, category, type, tags);
        for (DishTopping t : tops) dish.addTopping(t);
        return dish;
    }

    private static <T> T first(T a, T b) { return a != null ? a : b; }
}
