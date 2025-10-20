package Restaurant;

import Dish.*;
import Restaurant.Enum.cuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.RestaurantExist;

import java.time.LocalTime;
import java.util.List;

public class RestaurantService {
    private static RestaurantRepo repo;
    private RestaurantFilter filter;

    public RestaurantService(RestaurantRepo r){
        repo = r;
        this.filter=new RestaurantFilter(repo);
    }

    public static void addRestaurant(Restaurant restaurant) throws RestaurantExist {
        if (repo.restaurantExist(restaurant)) {
            throw new RestaurantExist("Le restaurant existe déjà dans le repo");
        }
        repo.addRestaurant(restaurant);
    }

    // --- Méthodes de filtrage exposées par le service ---
    public List<Restaurant> findByName(String name){
        return filter.findName(name);
    }

    public List<Restaurant> filterByType(establishmentType type){
        return filter.filterByType(type);
    }

    public List<Restaurant> filterByAvailability(){
        return filter.filterByAvailability();
    }

    public List<Restaurant> filterByAvailability(LocalTime at){
        return filter.filterByAvailability(at);
    }

    public List<Restaurant> filterByCuisine(cuisineType type){
        return filter.filterByCuisineType(type);
    }

    public List<Restaurant> filterByTag(DishTag tag){
        return filter.filterByTag(tag);
    }

    public List<Restaurant> filterByAllTags(List<DishTag> tags){
        return filter.filterByAllTags(tags);
    }

    public List<Restaurant> filterByPriceMin(double min){
        return filter.filterByPriceMin(min);
    }

    public List<Restaurant> filterByPriceMax(double max){
        return filter.filterByPriceMax(max);
    }

    public List<Restaurant> filterByPriceRange(double min, double max){
        return filter.filterByPriceRange(min, max);
    }


}
