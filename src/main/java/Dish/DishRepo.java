package Dish;

import Order.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DishRepo {
    private Map<Integer,Dish> dishes;
    public DishRepo(){
        dishes=new HashMap<Integer, Dish>();
    }
    public Optional<Dish> findById(int id){
        return Optional.ofNullable(dishes.get(id));
    }
    public Dish save(Dish dish){
        dishes.put(dish.getId(),dish);
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
    @Override
    public String toString(){
        return dishes.toString();
    }

}
