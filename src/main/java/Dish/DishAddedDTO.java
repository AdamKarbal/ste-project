package Dish;

import java.util.Map;

public record DishAddedDTO(int idOrder, Map<Integer,Integer> dishes, double price) {

}
