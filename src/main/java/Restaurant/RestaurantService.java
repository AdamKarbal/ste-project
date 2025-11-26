package Restaurant;

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

    private RestaurantRepo repo;
    private RestaurantFilter filter;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public RestaurantService(RestaurantRepo r) {
        this.repo = r;
        this.filter = new RestaurantFilter(repo);
    }

    public RestaurantService(){
        this.repo = new RestaurantRepo(new ArrayList<Restaurant>());
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
        this.refreshRestaurantsFromServer();
        return filter.findName(name);
    }

    public List<Restaurant> filterByType(establishmentType type) {
        this.refreshRestaurantsFromServer();
        return filter.filterByType(type);
    }

    public List<Restaurant> filterByAvailability() {
        this.refreshRestaurantsFromServer();
        return filter.filterByAvailability();
    }

    public List<Restaurant> filterByAvailability(LocalTime at) {
        this.refreshRestaurantsFromServer();
        return filter.filterByAvailability(at);
    }

    public List<Restaurant> filterByCuisine(CuisineType type) {
        this.refreshRestaurantsFromServer();
        return filter.filterByCuisineType(type);
    }

    public List<Restaurant> filterByTag(DishTag tag) {
        this.refreshRestaurantsFromServer();
        return filter.filterByTag(tag);
    }

    public List<Restaurant> filterByAllTags(List<DishTag> tags) {
        this.refreshRestaurantsFromServer();
        return filter.filterByAllTags(tags);
    }

    public List<Restaurant> filterByPriceMin(double min) {
        this.refreshRestaurantsFromServer();
        return filter.filterByPriceMin(min);
    }

    public List<Restaurant> filterByPriceMax(double max) {
        this.refreshRestaurantsFromServer();
        return filter.filterByPriceMax(max);
    }

    public List<Restaurant> filterByPriceRange(double min, double max) {
        this.refreshRestaurantsFromServer();
        return filter.filterByPriceRange(min, max);
    }

    public List<Restaurant> filter(FilterCriteria c ){
        this.refreshRestaurantsFromServer();
        return filter.filter(c);
    }

    public List<Restaurant> refreshRestaurantsFromServer() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8002/api/restaurantrepo/"))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur GET restaurants: " + response.body());
            }

            // Désérialisation JSON -> List<Restaurant>
            List<Restaurant> restaurants = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Restaurant.class)
            );

            // Mise à jour du repo interne
            repo.updateRestaurants(restaurants);
            filter.updateRepo(repo);
            return restaurants;

        } catch (Exception e) {
            throw new RuntimeException("Erreur HTTP GET: " + e.getMessage(), e);
        }
    }
}
