package Dish;

import Order.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DishRepo {
    private Map<Integer,Dish> dishes;
    private File file;
    private ObjectMapper mapper;

    public DishRepo(List<Dish> initialData) {
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.file = new File("dishes.json");
        this.dishes = new HashMap<>();
        loadFromFile();
        if (this.dishes.isEmpty() && initialData != null && !initialData.isEmpty()) {
            for (Dish d : initialData) {
                save(d);
            }
        }
    }

    private void saveToFile() {
        try {
            mapper.writeValue(file, dishes);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde DishRepo : " + e.getMessage());
        }
    }

    private void loadFromFile() {
        if (file.exists()) {
            try {
                Map<Integer, Dish> loadedMap = mapper.readValue(
                        file,
                        new TypeReference<Map<Integer, Dish>>() {}
                );
                if (loadedMap != null) {
                    this.dishes.clear();
                    this.dishes.putAll(loadedMap);
                    System.out.println("📂 " + dishes.size() + " plats chargés depuis dishes.json");
                }
            } catch (IOException e) {
                System.err.println(" Erreur lecture dishes.json : " + e.getMessage());
            }
        }
    }
    public Optional<Dish> findById(int id){
        return Optional.ofNullable(dishes.get(id));
    }
    public Dish save(Dish dish) {
        dishes.put(dish.getId(), dish);
        saveToFile();
        return dish;
    }

    public List<Dish> getAllDishes(){
        return dishes.values().stream().toList();
    }

    public boolean deleteById(int id){
        if(dishes.containsKey(id)){
            dishes.remove(id);
            return true;
        }
        return false;
    }
    public boolean updateDish(int id, Dish updatedDish) {
        Dish oldDish = findById(id).orElseThrow(
                () -> new DishNotFound("Plat " + id + " introuvable."));
        if (oldDish!=null) {
            dishes.remove(id);
            dishes.put(id,updatedDish);
            return true;
        }
        return false;
    }

    public void ClearFile(){
        this.dishes.clear();
        saveToFile();
    }
        @Override
    public String toString(){
        return dishes.toString();
    }

}
