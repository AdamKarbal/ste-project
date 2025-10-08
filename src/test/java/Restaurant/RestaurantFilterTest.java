package Restaurant;


import Dish.DishTag;
import Dish.Dish;
import Restaurant.Enum.cuisineType;
import Restaurant.Enum.establishmentType;
import Utils.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        Dish dishSansGlutenEtVegan = new Dish();
        dishSansGlutenEtVegan.getTags().addAll(List.of(DishTag.SANS_GLUTEN, DishTag.VEGAN));

        Dish dishVegan = new Dish();
        dishVegan.getTags().add(DishTag.VEGAN);

        Dish dishSansGluten = new Dish();
        dishSansGluten.getTags().add(DishTag.SANS_GLUTEN);

        Dish dishEpice = new Dish();
        dishEpice.getTags().add(DishTag.EPICE);

        Dish cheapDish = new Dish();
        cheapDish.setPrice(10.0);

        Dish mediumDish = new Dish();
        mediumDish.setPrice(20.0);

        Dish expensiveDish = new Dish();
        expensiveDish.setPrice(30.0);


        // Restaurant always open
        Date alwaysOpenStart = new Date.Builder()
                .day(1).month(1).year(2025)
                .hour(0).minute(0)
                .build();
        Date alwaysOpenEnd = new Date.Builder()
                .day(1).month(1).year(2030)
                .hour(23).minute(59)
                .build();
        RestaurantSchedule alwaysOpen = new RestaurantSchedule(alwaysOpenStart, alwaysOpenEnd, 100);

        // Restaurant never open
        Date neverOpenStart = new Date.Builder()
                .day(1).month(1).year(2025)
                .hour(1).minute(0)
                .build();
        Date neverOpenEnd = new Date.Builder()
                .day(1).month(1).year(2025)
                .hour(1).minute(0)
                .build();
        RestaurantSchedule neverOpen = new RestaurantSchedule(neverOpenStart, neverOpenEnd, 0);

        restoA = new Restaurant.Builder()
                .name("Pizza Palace")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(List.of(alwaysOpen))     // always
                .dishes(List.of(dishSansGluten, dishVegan,dishSansGlutenEtVegan))
                .cuisineType(List.of(cuisineType.ITALIENNE))
                .build();

        restoB = new Restaurant.Builder()
                .name("La pizza roma")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(List.of(neverOpen))      // never
                .dishes(List.of(dishVegan))
                .cuisineType(List.of(cuisineType.ITALIENNE))
                .build();

        restoC = new Restaurant.Builder()
                .name("Burger King")
                .establishmentType(establishmentType.FAST_FOOD)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishEpice))
                .cuisineType(List.of(cuisineType.AMERICAINE))
                .build();

        restoD = new Restaurant.Builder()
                .name("Pasta House")
                .establishmentType(establishmentType.RESTAURANT)
                .restaurantSchedules(new ArrayList<>())
                .dishes(List.of(dishSansGluten))
                .cuisineType(List.of(cuisineType.FRANCAISE))
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
        List<Restaurant> result = filter.filterByType(establishmentType.HOTEL);
        assertTrue(result.isEmpty());
    }

    //Filter by availibility

    @Test
    void filterByAvailibility_shouldReturnOnlyAlwaysOpenRestaurant() {
        List<Restaurant> result = filter.filterByAvailibility();
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().equals("Pizza Palace"));
    }

    @Test
    void filterByAvailibility_shouldReturnEmptyIfAllClosed() {
        // on rend tous les restaurants "jamais ouverts"
        restoA.setRestaurantSchedules(restoB.getRestaurantSchedules());
        List<Restaurant> result = filter.filterByAvailibility();
        assertTrue(result.isEmpty());
    }

    @Test
    void filterByAvailibility_atSpecificTime() throws Exception {
        // On choisit une date comprise dans le créneau du resto toujours ouvert
        Date target = new Date.Builder()
                .day(1).month(6).year(2026)
                .hour(12).minute(0)
                .build();

        List<Restaurant> result = filter.filterByAvailibility(target);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().equals("Pizza Palace"));
    }


    // Filter by CuisineType

    @Test
    void filterByCuisineType_shouldReturnMatchingRestaurants() {
        // On ajoute un type de cuisine à certains restos
        restoA.setCuisineTypes(List.of(cuisineType.ITALIENNE, cuisineType.FRANCAISE));
        restoB.setCuisineTypes(List.of(cuisineType.AMERICAINE));
        restoC.setCuisineTypes(List.of(cuisineType.ITALIENNE));

        List<Restaurant> result = filter.filterByCuisineType(cuisineType.ITALIENNE);
        assertEquals(2, result.size());
        assertTrue(result.contains(restoA));
        assertTrue(result.contains(restoC));
    }

    @Test
    void filterByCuisineType_shouldReturnEmptyIfNoMatch() {
        restoA.setCuisineTypes(List.of(cuisineType.FRANCAISE));
        restoB.setCuisineTypes(List.of(cuisineType.AMERICAINE));
        restoC.setCuisineTypes(List.of(cuisineType.CHINOISE));

        List<Restaurant> result = filter.filterByCuisineType(cuisineType.MEXICAINE);
        assertTrue(result.isEmpty());
    }

    //Filter by tag

    @Test
    void filterByTag_shouldReturnRestaurantsWithGivenTag() {
        List<Restaurant> result = filter.filterByTag(DishTag.SANS_GLUTEN);
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
        List<Restaurant> result = filter.filterByAllTags(List.of(DishTag.SANS_GLUTEN, DishTag.VEGAN));
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
                new Dish() {{ setPrice(10); getTags().addAll(List.of(DishTag.SANS_GLUTEN, DishTag.VEGAN)); }},
                new Dish() {{ setPrice(20); getTags().add(DishTag.VEGAN); }}));
        restoB.setDishes(List.of(
                new Dish() {{ setPrice(30); getTags().add(DishTag.VEGAN); }}));
        restoC.setDishes(List.of(
                new Dish() {{ setPrice(20); getTags().add(DishTag.EPICE); }}));
        restoD.setDishes(List.of(
                new Dish() {{ setPrice(16); getTags().add(DishTag.SANS_GLUTEN); }}));
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

}