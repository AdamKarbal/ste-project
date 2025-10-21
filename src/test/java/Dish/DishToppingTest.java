package Dish;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DishToppingTest {

    // ---------- 1️⃣ Constructeur & getters ----------
    @Test
    void testConstructorAndGetters() {
        DishTopping topping = new DishTopping("Olives", 1.5f);

        assertEquals("Olives", topping.getName());
        assertEquals(1.5f, topping.getPrice());
    }

    // ---------- 2️⃣ toString ----------
    @Test
    void testToStringFormat() {
        DishTopping topping = new DishTopping("Fromage", 2.0f);
        String result = topping.toString();

        assertTrue(result.contains("Fromage"));
        assertTrue(result.contains("2.0"));
        assertEquals("Fromage (+ 2.0€)", result);
    }

    // ---------- 3️⃣ equals (insensible à la casse) ----------
    @Test
    void testEquals_IgnoresCase() {
        DishTopping t1 = new DishTopping("Tomates", 1.0f);
        DishTopping t2 = new DishTopping("tomates", 1.5f); // prix différent, mais même nom

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    // ---------- 4️⃣ equals négatif ----------
    @Test
    void testEquals_DifferentName_ShouldNotBeEqual() {
        DishTopping t1 = new DishTopping("Olives", 1.0f);
        DishTopping t2 = new DishTopping("Champignons", 1.0f);

        assertNotEquals(t1, t2);
    }

    // ---------- 5️⃣ equals réflexif, symétrique et transitif ----------
    @Test
    void testEqualsContract() {
        DishTopping a = new DishTopping("Cheese", 1.0f);
        DishTopping b = new DishTopping("cheese", 2.0f);
        DishTopping c = new DishTopping("CHEESE", 3.0f);

        // Réflexif
        assertEquals(a, a);

        // Symétrique
        assertTrue(a.equals(b) && b.equals(a));

        // Transitif
        assertTrue(a.equals(b) && b.equals(c) && a.equals(c));

        // Cohérence
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(b.hashCode(), c.hashCode());
    }

    // ---------- 6️⃣ equals avec objet différent ----------
    @Test
    void testEquals_WithDifferentObjectType_ShouldReturnFalse() {
        DishTopping topping = new DishTopping("Olives", 1.0f);
        String other = "Olives";
        assertNotEquals(topping, other);
    }

    // ---------- 7️⃣ equals avec null ----------
    @Test
    void testEquals_WithNull_ShouldReturnFalse() {
        DishTopping topping = new DishTopping("Olives", 1.0f);
        assertNotEquals(null, topping);
    }
}
