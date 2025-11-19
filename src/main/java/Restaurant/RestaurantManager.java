package Restaurant;
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

    public void addDish(Dish d, String name) throws DishInDishes {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if(restaurant.alreadyDish(d)) throw new DishInDishes("Le plat "+ d+ " est déjà dans le restaurant." ) ;
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


    public void addEstablishmentType(establishmentType e, String name) throws EstablishmentTypeAlreadySet {
        Restaurant restaurant = getRestaurantFromRepo(name);
        if (restaurant.getEstablishmentType() != null)
            throw new EstablishmentTypeAlreadySet("Le restaurant a déjà un type d'établissement défini.");
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

    public Dish createDish(DishInput input, String name) throws DishInDishes {
        Dish dish = smartDishFactory.create(input);
        this.addDish(dish,name); // tes règles (doublon, etc.)
        return dish;
    }

    public Restaurant getRestaurantFromRepo(String name) {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8002/api/restaurantrepo/" + encodedName))
                .GET()
                .build();

        CompletableFuture<Restaurant> futureRestaurant = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    // AJOUTE CECI : Vérification du code HTTP
                    if (response.statusCode() == 404) {
                        throw new RuntimeException("Restaurant non trouvé sur le serveur : " + name);
                    }
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Erreur serveur (" + response.statusCode() + ") : " + response.body());
                    }
                    return response.body(); // On passe le corps seulement si c'est OK
                })
                .thenApply(corps -> {
                    System.out.println("JSON reçu: " + corps);
                    try {
                        Map<String, Object> attributes = mapper.readValue(corps, Map.class);
                        String restaurantName = (String) attributes.get("name");

                        // 1. Gestion NULL establishmentType
                        String typeStr = (String) attributes.get("establishmentType");
                        establishmentType type = (typeStr != null) ? establishmentType.valueOf(typeStr) : null;

                        // 2. Parsing robuste des listes avec TypeReference
                        ArrayList<Dish> dishes = mapper.convertValue(
                                attributes.get("dishes"),
                                new TypeReference<ArrayList<Dish>>() {}
                        );

                        ArrayList<RestaurantSchedule> schedules = mapper.convertValue(
                                attributes.get("restaurantSchedules"),
                                new TypeReference<ArrayList<RestaurantSchedule>>() {}
                        );

                        // 3. Parsing robuste des CuisineType
                        // On convertit d'abord en List<String> pour être sûr
                        ArrayList<String> cuisineStrings = mapper.convertValue(
                                attributes.get("cuisineTypes"),
                                new TypeReference<ArrayList<String>>() {}
                        );

                        ArrayList<CuisineType> cuisineTypes = new ArrayList<>();
                        if (cuisineStrings != null) {
                            for (String s : cuisineStrings) {
                                cuisineTypes.add(CuisineType.valueOf(s));
                            }
                        }

                        return new Restaurant.Builder()
                                .name(restaurantName)
                                .dishes(dishes)
                                .restaurantSchedules(schedules)
                                .cuisineType(cuisineTypes)
                                .establishmentType(type)
                                .build();

                    } catch (Exception e) {
                        throw new RuntimeException("Échec du parsing JSON du restaurant: " + e.getMessage(), e);
                    }
                });

        try {
            return futureRestaurant.join();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de récupérer le restaurant: " + name, e);
        }
    }

    public void saveRestaurant(Restaurant restaurant) {
        try {
            String restaurantJson = mapper.writeValueAsString(restaurant);
            String name = restaurant.getName();
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8002/api/restaurantrepo/" + encodedName))
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
