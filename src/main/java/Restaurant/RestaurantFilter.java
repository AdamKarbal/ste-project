package Restaurant;

import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class RestaurantFilter {
    RestaurantRepo restaurantRepo;
    public RestaurantFilter(RestaurantRepo restaurantRepo) {
        this.restaurantRepo = restaurantRepo;
    }

    public List<Restaurant> filterByType(establishmentType type) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant -> restaurant.getEstablishmentType().equals(type))
                .toList() ;
    }

    public List<Restaurant> filterByAvailability() {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(Restaurant::isOpenNow)
                .toList();
    }

    public List<Restaurant> filterByAvailability(LocalTime targetTime) {
        LocalDateTime targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime);

        return restaurantRepo.getRestaurants()
                .stream()
                .filter(r -> r.isOpenAt(targetDateTime)) // ← utilise la version LocalDateTime de isOpenAt
                .toList();
    }

    public List<Restaurant> findName(String name) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant -> restaurant.getName().toLowerCase().contains(name.toLowerCase()))
                .toList() ;
    }

    public List<Restaurant> filterByCuisineType(CuisineType type) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant -> restaurant.getCuisineTypes().stream().anyMatch(cuisineType -> cuisineType.equals(type)))
                .toList() ;
    }

    public List<Restaurant> filterByTag(DishTag tag) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .flatMap(dish -> dish.getDishTag().stream())
                                .anyMatch(t -> t.equals(tag))
                )
                .toList();
    }

    public List<Restaurant> filterByAllTags(List<DishTag> tags) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .anyMatch(dish -> dish.getDishTag().containsAll(tags))
                )
                .toList();
    }

    public List<Restaurant> filterByPriceMin(double minPrice) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .anyMatch(dish -> dish.getPrice() >= minPrice)
                )
                .toList();
    }

    public List<Restaurant> filterByPriceMax(double maxPrice) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .anyMatch(dish -> dish.getPrice() <= maxPrice)
                )
                .toList();
    }

    public List<Restaurant> filterByPriceRange(double minPrice, double maxPrice) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .anyMatch(dish ->
                                        dish.getPrice() >= minPrice && dish.getPrice() <= maxPrice
                                )
                )
                .toList();
    }

}
