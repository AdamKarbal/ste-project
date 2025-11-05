package Restaurant;


import Dish.DishTag;
import Dish.Dish;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantFilterTest {
    private RestaurantRepo restaurantRepo;
    private RestaurantFilter filter;
    private Restaurant restoA;
    private Restaurant restoB;
    private Restaurant restoC;
    private Restaurant restoD;

    @BeforeEach
    void setUp() throws Exception {

        //Dish
        Dish dishSansGlutenEtVegan = new Dish(0f, "Dish SG+V", null, null, null,
                new ArrayList<>(List.of(DishTag.GLUTEN_FREE, DishTag.VEGAN)));

        Dish dishVegan = new Dish(0f, "Dish Vegan",null, null, null,
                new ArrayList<>(List.of(DishTag.VEGAN)));

        Dish dishSansGluten = new Dish(0f, "Dish SG", null, null, null,
                new ArrayList<>(List.of(DishTag.GLUTEN_FREE)));

        Dish dishEpice = new Dish(0f, "Dish Spicy", null, null, null,
                new ArrayList<>(List.of(DishTag.SPICY)));

        Dish cheapDish = new Dish(10f, "Dish Cheap", null, null, null,
                new ArrayList<>());

        Dish mediumDish = new Dish(20f, "Dish Medium", null, null, null,
                new ArrayList<>());

        Dish expensiveDish = new Dish(30f, "Dish Expensive", null, null, null,
                new ArrayList<>());

        restoA = new Restaurant.Builder()
                .name("Pizza Palace")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(new ArrayList<>())     // always
                .dishes(List.of(dishSansGluten, dishVegan,dishSansGlutenEtVegan))
                .cuisineType(List.of(CuisineType.ITALIENNE))
                .build();

        for (DayOfTheWeek day : DayOfTheWeek.getRealDays()) {
            RestaurantScheduleManager.addSchedule(restoA, day, LocalTime.of(0, 0), LocalTime.of(23, 59), 100);
        }

        restoB = new Restaurant.Builder()
                .name("La pizza roma")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishVegan))
                .cuisineType(List.of(CuisineType.ITALIENNE))
                .build();

        restoC = new Restaurant.Builder()
                .name("Burger King")
                .establishmentType(establishmentType.FAST_FOOD)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishEpice))
                .cuisineType(List.of(CuisineType.AMERICAINE))
                .build();

        restoD = new Restaurant.Builder()
                .name("Pasta House")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishSansGluten))
                .cuisineType(List.of(CuisineType.FRANCAISE))
                .build();

        restoA.setDishes(List.of(
                cheapDish, mediumDish, dishSansGluten, dishVegan, dishSansGlutenEtVegan
        ));
        restoB.setDishes(List.of(expensiveDish, dishVegan));
        restoC.setDishes(List.of(mediumDish, dishEpice));
        restoD.setDishes(List.of(dishSansGluten));

        restaurantRepo = new RestaurantRepo(List.of(restoA, restoB, restoC, restoD));
        filter = new RestaurantFilter(restaurantRepo);
    }


    // Filter by name
    @Test
    void findName_partialMatchPizza() {
        List<Restaurant> result = filter.findName("pizza");
        assertEquals(2, result.size());
        assertTrue(result.contains(restoA));
        assertTrue(result.contains(restoB));
    }

    @Test
    void findName_caseInsensitive() {
        List<Restaurant> result = filter.findName("PIZZA");
        assertEquals(2, result.size());
    }

    @Test
    void findName_noMatch() {
        List<Restaurant> result = filter.findName("sushi");
        assertTrue(result.isEmpty());
    }

    //Filter by type

    @Test
    void filterByType_returnAllRestaurants() {
        List<Restaurant> result = filter.filterByType(establishmentType.RESTAURANT);
        assertEquals(3, result.size());
        assertTrue(result.contains(restoA));
        assertTrue(result.contains(restoB));
    }

    @Test
    void filterByType_noMatch() {
        List<Restaurant> result = filter.filterByType(establishmentType.SUSHI_BAR);
        assertTrue(result.isEmpty());
    }

    //Filter by availibility

    @Test
    void filterByAvailability_shouldReturnOnlyAlwaysOpenRestaurant() {
        List<Restaurant> result = filter.filterByAvailability();
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().equals("Pizza Palace"));
    }

    @Test
    void filterByAvailability_shouldReturnEmptyIfAllClosed() {
        // on rend tous les restaurants "jamais ouverts"
        restoA.setRestaurantSchedules(restoB.getRestaurantSchedules());
        List<Restaurant> result = filter.filterByAvailability();
        assertTrue(result.isEmpty());
    }

    @Test
    void filterByAvailability_atSpecificTime() throws Exception {
        // On choisit une date comprise dans le créneau du resto toujours ouvert
        LocalTime target = LocalTime.of(12, 0);

        List<Restaurant> result = filter.filterByAvailability(target);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().equals("Pizza Palace"));
    }


    // Filter by CuisineType

    @Test
    void filterByCuisineType_shouldReturnMatchingRestaurants() {
        // On ajoute un type de cuisine à certains restos
        restoA.setCuisineTypes(List.of(CuisineType.ITALIENNE, CuisineType.FRANCAISE));
        restoB.setCuisineTypes(List.of(CuisineType.AMERICAINE));
        restoC.setCuisineTypes(List.of(CuisineType.ITALIENNE));

        List<Restaurant> result = filter.filterByCuisineType(CuisineType.ITALIENNE);
        assertEquals(2, result.size());
        assertTrue(result.contains(restoA));
        assertTrue(result.contains(restoC));
    }

    @Test
    void filterByCuisineType_shouldReturnEmptyIfNoMatch() {
        restoA.setCuisineTypes(List.of(CuisineType.FRANCAISE));
        restoB.setCuisineTypes(List.of(CuisineType.AMERICAINE));
        restoC.setCuisineTypes(List.of(CuisineType.CHINOISE));

        List<Restaurant> result = filter.filterByCuisineType(CuisineType.MEXICAINE);
        assertTrue(result.isEmpty());
    }

    //Filter by tag

    @Test
    void filterByTag_shouldReturnRestaurantsWithGivenTag() {
        List<Restaurant> result = filter.filterByTag(DishTag.GLUTEN_FREE);
        assertEquals(2, result.size());
        assertTrue(result.contains(restoA));
        assertTrue(result.contains(restoD));
    }

    @Test
    void filterByTag_shouldReturnEmptyIfNoRestaurantHasTag() {
        List<Restaurant> result = filter.filterByTag(DishTag.HALAL);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterByAllTags_shouldReturnRestaurantsWithAllTags() {
        List<Restaurant> result = filter.filterByAllTags(List.of(DishTag.GLUTEN_FREE, DishTag.VEGAN));
        assertEquals(1, result.size());
        assertTrue(result.contains(restoA));
    }


    //filter by price

    @Test
    void filterByPriceMin_shouldReturnRestaurantsWithDishAboveMinPrice() {
        List<Restaurant> result = filter.filterByPriceMin(25.0);
        assertEquals(1, result.size());
        assertTrue(result.contains(restoB)); // seul restoB a un plat à 30€
    }

    @Test
    void filterByPriceMax_shouldReturnRestaurantsWithDishBelowMaxPrice() {
        restoA.setDishes(List.of(
                new Dish(10f, "Dish A1",  null, null, null,
                        new ArrayList<>(List.of(DishTag.GLUTEN_FREE, DishTag.VEGAN))),
                new Dish(20f, "Dish A2",  null, null, null,
                        new ArrayList<>(List.of(DishTag.VEGAN)))
        ));

        restoB.setDishes(List.of(
                new Dish(30f, "Dish B1",  null, null, null,
                        new ArrayList<>(List.of(DishTag.VEGAN)))
        ));

        restoC.setDishes(List.of(
                new Dish(20f, "Dish C1",  null, null, null,
                        new ArrayList<>(List.of(DishTag.SPICY)))
        ));

        restoD.setDishes(List.of(
                new Dish(16f, "Dish D1", null, null, null,
                        new ArrayList<>(List.of(DishTag.GLUTEN_FREE)))
        ));

        List<Restaurant> result = filter.filterByPriceMax(15.0);
        assertEquals(1, result.size());
        assertTrue(result.contains(restoA));
    }

    @Test
    void filterByPriceRange_shouldReturnRestaurantsWithDishInRange() {
        List<Restaurant> result = filter.filterByPriceRange(10.0, 20.0);
        assertEquals(2, result.size());
        assertTrue(result.contains(restoA));
        assertTrue(result.contains(restoC));
    }

    @Test
    void filter_combinesMultipleCriteriaCorrectly() {
        // 🔹 1) On crée des critères combinés
        FilterCriteria criteria = FilterCriteria.builder()
                .cuisine(CuisineType.ITALIENNE)
                .priceMax(15.0)
                .availableAt(LocalTime.of(12, 0))
                .tagsAnyOf(List.of(DishTag.VEGAN))
                .build();

        // 🔹 2) On exécute le filtre global
        List<Restaurant> result = filter.filter(criteria);

        // 🔹 3) Vérification du résultat
        assertEquals(1, result.size(), "Un seul restaurant doit correspondre");
        assertTrue(result.contains(restoA), "Pizza Palace doit être le restaurant correspondant");
    }

    @Test
    void filter_returnsAllIfNoCriteria() {
        // 🔹 Aucun critère défini
        FilterCriteria criteria = FilterCriteria.builder().build();

        List<Restaurant> result = filter.filter(criteria);

        // 🔹 Tous les restaurants doivent être retournés
        assertEquals(4, result.size(), "Tous les restaurants doivent être présents sans filtre");
        assertTrue(result.containsAll(List.of(restoA, restoB, restoC, restoD)));
    }

    @Test
    void filter_filtersByNameAndCuisine() {
        // 🔹 Nom contient "pasta" et cuisine française
        FilterCriteria criteria = FilterCriteria.builder()
                .nameContains("pasta")
                .cuisine(CuisineType.FRANCAISE)
                .build();

        List<Restaurant> result = filter.filter(criteria);

        // 🔹 Seul Pasta House correspond
        assertEquals(1, result.size());
        assertTrue(result.contains(restoD));
    }

    @Test
    void filter_filtersByAllTags() {
        // 🔹 Tous les tags doivent être présents : VEGAN + GLUTEN_FREE
        FilterCriteria criteria = FilterCriteria.builder()
                .tagsAllOf(List.of(DishTag.VEGAN, DishTag.GLUTEN_FREE))
                .build();

        List<Restaurant> result = filter.filter(criteria);

        // 🔹 Seul Pizza Palace correspond
        assertEquals(1, result.size());
        assertTrue(result.contains(restoA));
    }












}