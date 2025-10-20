package Dish.suggest;

public interface DishSuggestionEngine {
    Suggestion suggest(String nameOrKeyword);
}
