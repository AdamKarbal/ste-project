package Dish.suggest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SuggestionTest {

    @Test
    void isEmpty_whenAllFieldsNullOrEmpty_returnsTrue() {
        Suggestion s = new Suggestion();
        assertTrue(s.isEmpty());
    }

    @Test
    void isEmpty_whenAnyFieldIsSet_returnsFalse() {
        Suggestion s1 = new Suggestion();
        s1.category = Dish.DishCategory.MAIN_COURSE;
        assertFalse(s1.isEmpty());

        Suggestion s2 = new Suggestion();
        s2.suggestedPrice = 9.9;
        assertFalse(s2.isEmpty());
    }

}