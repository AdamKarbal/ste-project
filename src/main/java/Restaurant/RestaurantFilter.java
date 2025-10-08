package Restaurant;

import Dish.DishTag;
import Restaurant.Enum.cuisineType;
import Restaurant.Enum.establishmentType;
import Utils.Date;

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

    public List<Restaurant> filterByAvailibility() {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(Restaurant::isOpenNow)
                .toList();
    }

    public List<Restaurant> filterByAvailibility(Date targetTime) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(r -> r.isOpenAt(targetTime))
                .toList();
    }

    public List<Restaurant> findName(String name) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant -> restaurant.getName().toLowerCase().contains(name.toLowerCase()))
                .toList() ;
    }

    public List<Restaurant> filterByCuisineType(cuisineType type) {
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
                                .flatMap(dish -> dish.getTags().stream())
                                .anyMatch(t -> t.equals(tag))
                )
                .toList();
    }

    public List<Restaurant> filterByAllTags(List<DishTag> tags) {
        return restaurantRepo.getRestaurants()
                .stream()
                .filter(restaurant ->
                        restaurant.getDishes().stream()
                                .anyMatch(dish -> dish.getTags().containsAll(tags))
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
