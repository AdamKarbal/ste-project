package Dish.suggest;

import Dish.DishCategory;
import Dish.DishType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeywordSuggesterTest {

    @Test
    void suggest_pizza_margherita() {
        DishSuggestionEngine eng = new KeywordSuggester();
        Suggestion s = eng.suggest("Pizza Margherita");

        assertEquals(DishCategory.MAIN_COURSE, s.category);
        assertEquals(DishType.PIZZA, s.type);
        assertNotNull(s.suggestedPrice);
        assertTrue(s.toppings != null && s.toppings.size() >= 2);
    }
}