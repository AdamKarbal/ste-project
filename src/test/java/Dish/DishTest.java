package Dish;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires complets pour la classe Dish.
 */
public class DishTest {

    private Dish dish;
    private DishTopping olives;
    private DishTopping cheese;
    private List<DishTag> dishTag = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        olives = new DishTopping("Olives", 1.0f);
        cheese = new DishTopping("Fromage", 1.5f);

        dish = new Dish(
                10.0,
                "Pizza Margherita",
                "Pizza classique avec mozzarella et sauce tomate",
                DishCategory.MAIN_COURSE,
                DishType.PIZZA,
                dishTag
        );
        dishTag.add(DishTag.HALAL);
        dishTag.add(DishTag.VEGETARIAN);

    }

    // ---------- 1️⃣ Constructeur et getters/setters ----------
    @Test
    void testConstructorAndGetters() {
        assertEquals(10.0, dish.getPrice());
        assertEquals("Pizza Margherita", dish.getName());
        assertEquals("Pizza classique avec mozzarella et sauce tomate", dish.getDescription());
        assertEquals(DishCategory.MAIN_COURSE, dish.getCategory());
        assertEquals(DishType.PIZZA, dish.getType());
        assertTrue(dish.getDishTag().contains(DishTag.HALAL));
        assertTrue(dish.getDishTag().contains(DishTag.VEGETARIAN));
    }

    @Test
    void testSettersUpdateValues() {
        dish.setPrice(12.5);
        dish.setName("Pizza 4 Fromages");
        dish.setDescription("Pizza à base de mozzarella, chèvre, gorgonzola et emmental");
        dish.setCategory(DishCategory.MAIN_COURSE);
        dish.setType(DishType.OTHER);

        assertEquals(12.5, dish.getPrice());
        assertEquals("Pizza 4 Fromages", dish.getName());
        assertEquals("Pizza à base de mozzarella, chèvre, gorgonzola et emmental", dish.getDescription());
        assertEquals(DishType.OTHER, dish.getType());
    }

    // ---------- 2️⃣ Gestion des tags ----------
    @Test
    void testAddDishTag() {
        boolean added = dish.addDishTag(DishTag.SPICY);
        assertTrue(added);
        assertTrue(dish.getDishTag().contains(DishTag.SPICY));
    }

    @Test
    void testSetDishTagOverridesList() {
        dish.setDishTag(List.of(DishTag.VEGAN, DishTag.GLUTEN_FREE));
        assertEquals(2, dish.getDishTag().size());
        assertTrue(dish.getDishTag().contains(DishTag.VEGAN));
        assertFalse(dish.getDishTag().contains(DishTag.HALAL));
    }

    // ---------- 3️⃣ Gestion des toppings disponibles ----------
    @Test
    void testAddAvailableTopping() {
        dish.addAvailableTopping(olives);
        dish.addAvailableTopping(cheese);
        dish.addAvailableTopping(olives); // doublon ignoré
        assertEquals(2, dish.getAvailableToppings().size());
    }

    @Test
    void testRemoveAvailableTopping() {
        dish.addAvailableTopping(olives);
        dish.removeAvailableTopping(olives);
        assertFalse(dish.getAvailableToppings().contains(olives));
    }

    // ---------- 4️⃣ Gestion des toppings sélectionnés ----------
    @Test
    void testAddSelectedToppingValid() {
        dish.addAvailableTopping(olives);
        dish.addSelectedTopping(olives);
        assertEquals(1, dish.getSelectedToppings().size());
        assertTrue(dish.getSelectedToppings().contains(olives));
    }

    @Test
    void testAddSelectedToppingNotAvailableThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> dish.addSelectedTopping(cheese));
    }

    @Test
    void testRemoveSelectedTopping() {
        dish.addAvailableTopping(cheese);
        dish.addSelectedTopping(cheese);
        dish.removeSelectedTopping(cheese);
        assertFalse(dish.getSelectedToppings().contains(cheese));
    }

    // ---------- 5️⃣ Calcul du prix total ----------
    @Test
    void testGetToppingTotalPriceWithSelectedToppings() {
        dish.addAvailableTopping(olives);
        dish.addAvailableTopping(cheese);
        dish.addSelectedTopping(olives);
        dish.addSelectedTopping(cheese);

        double expected = 10.0 + 1.0 + 1.5;
        assertEquals(expected, dish.getToppingTotalPrice());
    }

    @Test
    void testGetToppingTotalPriceWithoutToppings() {
        assertEquals(10.0, dish.getToppingTotalPrice());
    }

    // ---------- 6️⃣ Défensive copy ----------
    @Test
    void testGetAvailableToppingsReturnsCopy() {
        dish.addAvailableTopping(olives);
        List<DishTopping> copy = dish.getAvailableToppings();
        copy.add(cheese); // on modifie la copie
        assertEquals(1, dish.getAvailableToppings().size()); // pas impacté
    }

    @Test
    void testGetSelectedToppingsReturnsCopy() {
        dish.addAvailableTopping(olives);
        dish.addSelectedTopping(olives);
        List<DishTopping> copy = dish.getSelectedToppings();
        copy.clear();
        assertEquals(1, dish.getSelectedToppings().size());
    }
}
