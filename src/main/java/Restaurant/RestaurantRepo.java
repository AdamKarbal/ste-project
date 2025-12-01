package Restaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class RestaurantRepo {
    private final List<Restaurant> restaurants;
    private ObjectMapper mapper;
    private File file;


    public RestaurantRepo(List<Restaurant> restaurants) {
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.file = new File("restaurants.json");
        this.restaurants = new ArrayList<>();
        loadFromFile();
        if (this.restaurants.isEmpty() && restaurants != null && !restaurants.isEmpty()) {
            this.restaurants.addAll(restaurants);
            saveToFile();
        }
    }

    private void saveToFile() {
        try {
            mapper.writeValue(file, restaurants);
            System.out.println("Sauvegarde fait dans :" + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    private void loadFromFile() {
        if (file.exists()) {
            try {
                List<Restaurant> loaded = mapper.readValue(file, new TypeReference<List<Restaurant>>() {});
                this.restaurants.clear();
                this.restaurants.addAll(loaded);
                System.out.println("Données chargées depuis " + file.getAbsolutePath() + " (" + restaurants.size() + " restos)");
            } catch (IOException e) {
                System.err.println("Impossible de lire le fichier (sera écrasé) : " + e.getMessage());
            }
        }
    }

    public List<Restaurant> getRestaurants() {
        return new ArrayList<>(restaurants);
    }

    public Restaurant addRestaurant(Restaurant resto) {
        Restaurant existing = getRestaurantByName(resto.getName());
        if (existing != null) {
            restaurants.remove(existing);
        }
        restaurants.add(resto);
        saveToFile();
        return resto;
    }

    public void updateRestaurants(List<Restaurant> restaurants){
        this.restaurants.clear();
        this.restaurants.addAll(restaurants);
    }

    public boolean restaurantExist(Restaurant resto){
        return restaurants.contains(resto);
    }

    public Restaurant getRestaurantByName(String name) {
        return restaurants.stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean updateRestaurant(String name, Restaurant updatedRestaurant) {
        boolean existed = restaurants.removeIf(r -> r.getName().equals(name));
        restaurants.add(updatedRestaurant);

        saveToFile();
        return existed;
    }

    public void clear(){
        restaurants.clear();
    }


}
