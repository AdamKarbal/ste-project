package Dish.suggest;

import Dish.*;
import java.util.List;
import java.util.stream.Collectors;

public class Suggestion {
    public String description;          // ex: texte par défaut
    public DishCategory category;
    public DishType type;
    public Double suggestedPrice;       // prix "proposé" (pas imposé)
    public List<DishTag> tags;
    public List<DishTopping> toppings;

    public boolean isEmpty() {
        return description == null
                && category == null
                && type == null
                && suggestedPrice == null
                && (tags == null || tags.isEmpty())
                && (toppings == null || toppings.isEmpty());
    }

    @Override
    public String toString() {
        String tagsStr = (tags == null || tags.isEmpty())
                ? "-"
                : tags.stream().map(Enum::name).collect(Collectors.joining(", "));
        String topsStr = (toppings == null || toppings.isEmpty())
                ? "-"
                : toppings.stream().map(DishTopping::toString).collect(Collectors.joining(", "));

        return "Suggestion{\n" +
                "  category=" + (category != null ? category : "-") + ",\n" +
                "  type=" + (type != null ? type : "-") + ",\n" +
                "  suggestedPrice=" + (suggestedPrice != null ? suggestedPrice : "-") + ",\n" +
                "  tags=[" + tagsStr + "],\n" +
                "  toppings=[" + topsStr + "],\n" +
                "  description=" + (description != null ? ('"' + description + '"') : "-") + "\n" +
                "}";
    }
}
