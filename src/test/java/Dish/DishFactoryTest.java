package Dish;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DishFactoryTest {

    @Test
    void create_userOverridesPrice_andMergesToppings() {
        DishFactory factory = new DishFactory();

        DishInput in = new DishInput();
        in.name = "Pizza Margherita";                 // déclenche PIZZA
        in.price = 12.0;                              // l’utilisateur prime
        in.toppings = List.of(new DishTopping("Olives", 0.8f)); // ajout

        Dish d = factory.create(in);

        assertEquals("Pizza Margherita", d.getName());
        assertEquals(DishType.PIZZA, d.getType());    // vient de la suggestion
        assertEquals(12.0, d.getPrice(), 1e-6);       // user > suggestion
        assertTrue(d.getToppings().stream().anyMatch(t -> t.getName().equalsIgnoreCase("Olives")));
        // merge : Mozzarella, Basilic (suggérés) + Olives (user)
        assertTrue(d.getToppings().size() >= 3);
    }

    @Test
    void create_whenNoSuggestion_usesUserFields() {
        DishFactory factory = new DishFactory();

        DishInput in = new DishInput();
        in.name = "Plat mystère";
        in.type = DishType.OTHER;
        in.category = DishCategory.MAIN_COURSE;
        in.price = 9.9;

        Dish d = factory.create(in);

        assertEquals("Plat mystère", d.getName());
        assertEquals(DishType.OTHER, d.getType());
        assertEquals(DishCategory.MAIN_COURSE, d.getCategory());
        assertEquals(9.9, d.getPrice(), 1e-6);
    }

    @Test
    void create_negativePrice_throws() {
        DishFactory factory = new DishFactory();
        DishInput in = new DishInput();
        in.name = "Burger";
        in.price = -1.0;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> factory.create(in));
        assertTrue(ex.getMessage().toLowerCase().contains("price"));
    }
}
