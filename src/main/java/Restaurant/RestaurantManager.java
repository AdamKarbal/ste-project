package Restaurant;
import Api.Utils.Requester;
import Dish.*;
import Dish.suggest.Suggestion;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class RestaurantManager {
    //Instances
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final SmartDishFactory smartDishFactory = new SmartDishFactory();

    //Constructeur
    public RestaurantManager(){
    }


    //Méthodes
    // Pourquoi on fait comme ca ? parceque c'est au manager de demandé au restaurant l'état est en fonction il choisit quoi faire
    // c'est pas au restaurant de le faire même si il verifie.
    public void updateSchedule(DayOfTheWeek day, LocalTime start, LocalTime end, int capacity,String name) {
        Restaurant restaurant = getRestaurantFromRepo(name);
        RestaurantScheduleManager.updateSchedule(restaurant, day, start, end, capacity);
        saveRestaurant(restaurant);
    }


    public void updateCapacity(DayOfTheWeek day, LocalTime start, LocalTime end, int capacity, String name) throws ScheduleNoExist {
        Restaurant restaurant = getRestaurantFromRepo(name);
        RestaurantScheduleManager.updateCapacity(restaurant, day, start, end, capacity);
        saveRestaurant(restaurant);
    }

    public void addDish(Dish d, String name) throws DishAlreadyInDishes {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if(restaurant.alreadyDish(d)) throw new DishAlreadyInDishes("Le plat "+ d+ " est déjà dans le restaurant." ) ;
        restaurant.addDish(d);
        saveRestaurant(restaurant);
    }

    public void updateDish(Dish old, Dish replacement,String name) throws dishNoInDishes {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if(!(restaurant.alreadyDish(old))) throw new dishNoInDishes("Impossible de mettre à jour le plat, il n'est pas présent.");
        restaurant.removeDish(old);
        restaurant.addDish(replacement);
        saveRestaurant(restaurant);
    }

    public void removeDish(Dish d, String name) throws dishNoInDishes{
        Restaurant restaurant = getRestaurantFromRepo(name);
        if(!(restaurant.alreadyDish(d))) throw new dishNoInDishes("Impossible d'enlever le plat :" + d + ", il n'est pas présent dans le restaurant.");
        restaurant.removeDish(d);
        saveRestaurant(restaurant);
    }

    public void addCuisineType(CuisineType c, String name) throws CuisineTypeInList {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if (restaurant.alreadyCuisineType(c))
            throw new CuisineTypeInList("Le type de cuisine " + c + " est déjà ajouté.");
        restaurant.addCuisineType(c);
        saveRestaurant(restaurant);
    }

    public void removeCuisineType(CuisineType c,String name) throws CuisineTypeNoExist {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if (!(restaurant.alreadyCuisineType(c)))
            throw new CuisineTypeNoExist("Le type de cuisine " + c + " n'existe pas dans le restaurant.");
        restaurant.removeCuisineType(c);
        saveRestaurant(restaurant);
    }


    public void setEstablishmentType(establishmentType e, String name) {
        Restaurant restaurant = getRestaurantFromRepo(name);
        restaurant.setEstablishmentType(e);
        saveRestaurant(restaurant);
    }

    public void removeEstablishmentType(String name) throws EstablishmentTypeNoExist {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if (restaurant.getEstablishmentType() == null)
            throw new EstablishmentTypeNoExist("Le restaurant n'a pas de type d'établissement défini.");
        restaurant.setEstablishmentType(null);
        saveRestaurant(restaurant);
    }

    public DishTopping createToppingForDish(Dish dish, String name, float price) {
        DishTopping topping = new DishTopping(name, price);
        dish.addAvailableTopping(topping);
        return topping;
    }
    public Suggestion previewSuggestion(String nameOrKeyword) {
        return smartDishFactory.suggest(nameOrKeyword);
    }

    public Dish createDish(DishInput input,String name) throws DishAlreadyInDishes {
        Dish dish = smartDishFactory.create(input);
        this.addDish(dish, name); // tes règles (doublon, etc.)
        return dish;
    }

    public Restaurant getRestaurantFromRepo(String name) {
        return Requester.getRestaurantByName(name);
    }

    public void saveRestaurant(Restaurant restaurant) {
        try {
            String restaurantJson = mapper.writeValueAsString(restaurant);
            String name = restaurant.getName();
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8002/api/restaurantrepo/" + encodedName+"/"))
                    .PUT(HttpRequest.BodyPublishers.ofString(restaurantJson))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new RuntimeException("Échec de la sauvegarde du restaurant: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur HTTP (PUT) lors de la sauvegarde: " + e.getMessage(), e);
        }
    }
}
