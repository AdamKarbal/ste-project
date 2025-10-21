package Dish;

import Dish.suggest.DishSuggestionEngine;
import Dish.suggest.KeywordSuggester;
import Dish.suggest.Suggestion;

import java.util.ArrayList;
import java.util.List;

public class SmartDishFactory {


    private final DishSuggestionEngine suggester = new KeywordSuggester();

    public SmartDishFactory() {
    }

    public Suggestion suggest(String nameOrKeyword) {
        return suggester.suggest(nameOrKeyword);
    }

    public Dish create(DishInput in) {
        if (in == null || in.name == null || in.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Dish name is required");
        }
        Suggestion s = suggester.suggest(in.name);

        final String name         = in.name.trim();
        final String description  = first(in.description,   s != null ? s.description   : null);
        final DishCategory category= first(in.category,     s != null ? s.category      : null);
        final DishType type       = first(in.type,          s != null ? s.type          : null);

        Double price = first(in.price, s != null ? s.suggestedPrice : null);
        if (price != null && price < 0) throw new IllegalArgumentException("Price must be >= 0");
        if (price == null) price = 0.0; // ou choisis de rendre obligatoire

        // Merge TAGS (suggestion ∪ saisie)
        List<DishTag> tags = new ArrayList<>();
        if (s != null && s.tags != null) tags.addAll(s.tags);
        if (in.tags != null) {
            tags.removeAll(in.tags);
            tags.addAll(in.tags);
        }

        List<DishTopping> tops = new ArrayList<>();
        if (s != null && s.toppings != null) tops.addAll(s.toppings);
        if (in.toppings != null) {
            for (DishTopping t : in.toppings) if (!tops.contains(t)) tops.add(t);
        }

        Dish dish = new Dish(price, name, description, category, type, tags);
        for (DishTopping t : tops) dish.addAvailableTopping(t);
        return dish;
    }

    private static <T> T first(T userValue, T suggestedValue) {
        return userValue != null ? userValue : suggestedValue;
    }
}
