package Dish;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DishFactoryTest {
    // ---------- 1️⃣ Test création valide ----------
    @Test
    void testCreateDish_ValidData_ShouldCreateDish() {
        Dish dish = DishFactory.createDish(
                12.5,
                "Pâtes Carbonara",
                "Pâtes fraîches à la crème et au lard",
                DishCategory.MAIN_COURSE,
                DishType.PASTA,
                List.of(DishTag.HALAL, DishTag.GLUTEN_FREE)
        );

        assertNotNull(dish);
        assertEquals(12.5, dish.getPrice());
        assertEquals("Pâtes Carbonara", dish.getName());
        assertEquals("Pâtes fraîches à la crème et au lard", dish.getDescription());
        assertEquals(DishCategory.MAIN_COURSE, dish.getCategory());
        assertEquals(DishType.PASTA, dish.getType());
        assertEquals(2, dish.getDishTag().size());
        assertTrue(dish.getDishTag().contains(DishTag.HALAL));
    }

    // ---------- 2️⃣ Test création sans tags ----------
    @Test
    void testCreateDish_WithoutTags_ShouldWork() {
        Dish dish = DishFactory.createDish(
                8.0,
                "Soupe de légumes",
                "Soupe maison aux légumes frais",
                DishCategory.STARTER,
                DishType.SOUP
        );

        assertNotNull(dish);
        assertEquals("Soupe de légumes", dish.getName());
        assertEquals(0, dish.getDishTag().size());
    }

    // ---------- 3️⃣ Validation du prix ----------
    @Test
    void testCreateDish_InvalidPrice_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DishFactory.createDish(
                        0,
                        "Pizza Margherita",
                        "Pizza classique",
                        DishCategory.MAIN_COURSE,
                        DishType.PIZZA
                )
        );
        assertEquals("Le prix du plat doit être supérieur à 0.", exception.getMessage());
    }

    // ---------- 4️⃣ Validation du nom ----------
    @Test
    void testCreateDish_NullName_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DishFactory.createDish(
                        10,
                        null,
                        "Plat sans nom",
                        DishCategory.MAIN_COURSE,
                        DishType.MEAT
                )
        );
        assertEquals("Le nom du plat ne peut pas être vide.", exception.getMessage());
    }

    @Test
    void testCreateDish_BlankName_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DishFactory.createDish(
                        10,
                        "   ",
                        "Nom vide",
                        DishCategory.MAIN_COURSE,
                        DishType.MEAT
                )
        );
        assertEquals("Le nom du plat ne peut pas être vide.", exception.getMessage());
    }

    // ---------- 5️⃣ Validation de la catégorie ----------
    @Test
    void testCreateDish_NullCategory_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DishFactory.createDish(
                        10,
                        "Burger",
                        "Burger maison",
                        null,
                        DishType.BURGER
                )
        );
        assertEquals("La catégorie du plat est obligatoire.", exception.getMessage());
    }

    // ---------- 6️⃣ Validation du type ----------
    @Test
    void testCreateDish_NullType_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DishFactory.createDish(
                        10,
                        "Salade César",
                        "Salade au poulet et parmesan",
                        DishCategory.STARTER,
                        null
                )
        );
        assertEquals("Le type du plat est obligatoire.", exception.getMessage());
    }

}