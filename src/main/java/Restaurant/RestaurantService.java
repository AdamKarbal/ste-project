package Restaurant;

import Api.Utils.Requester;
import Dish.*;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.RestaurantExist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RestaurantService {
    private final RestaurantRepo repo;
    private final RestaurantFilter filter;

    public RestaurantService(RestaurantRepo r) {
        this.repo = r;
        this.filter = new RestaurantFilter(repo);
    }

    public RestaurantService(){
        this.repo = new RestaurantRepo(new ArrayList<>());
        this.filter = new RestaurantFilter(repo);
    }

    public void addRestaurant(Restaurant restaurant) throws RestaurantExist {
        if (repo.restaurantExist(restaurant)) {
            throw new RestaurantExist("Le restaurant existe déjà dans le repo");
        }
        repo.addRestaurant(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        this.refreshRestaurantsFromServer();
        return repo.getRestaurants();
    }
    public Restaurant getRestaurantByName(String name) {
        this.refreshRestaurantsFromServer();
        return repo.getRestaurantByName(name);
    }

    // --- Méthodes de filtrage exposées par le service ---

    public List<Restaurant> findByName(String name) {
        refreshRestaurantsFromServer();
        return filter.findName(name);
    }

    public List<Restaurant> filterByType(establishmentType type) {
        refreshRestaurantsFromServer();
        return filter.filterByType(type);
    }

    public List<Restaurant> filterByAvailability() {
        refreshRestaurantsFromServer();
        return filter.filterByAvailability();
    }

    public List<Restaurant> filterByAvailability(LocalTime at) {
        refreshRestaurantsFromServer();
        return filter.filterByAvailability(at);
    }

    public List<Restaurant> filterByCuisine(CuisineType type) {
        refreshRestaurantsFromServer();
        return filter.filterByCuisineType(type);
    }

    public List<Restaurant> filterByTag(DishTag tag) {
        refreshRestaurantsFromServer();
        return filter.filterByTag(tag);
    }

    public List<Restaurant> filterByAllTags(List<DishTag> tags) {
        refreshRestaurantsFromServer();
        return filter.filterByAllTags(tags);
    }

    public List<Restaurant> filterByPriceMin(double min) {
        refreshRestaurantsFromServer();
        return filter.filterByPriceMin(min);
    }

    public List<Restaurant> filterByPriceMax(double max) {
        refreshRestaurantsFromServer();
        return filter.filterByPriceMax(max);
    }

    public List<Restaurant> filterByPriceRange(double min, double max) {
        refreshRestaurantsFromServer();
        return filter.filterByPriceRange(min, max);
    }

    public List<Restaurant> filter(FilterCriteria c ){
        refreshRestaurantsFromServer();
        return filter.filter(c);
    }

    public void refreshRestaurantsFromServer() {
        repo.updateRestaurants(Requester.getAllRestaurants());
        filter.updateRepo(repo);
    }
}
