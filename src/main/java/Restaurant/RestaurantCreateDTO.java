package Restaurant;

import Dish.Dish;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;

import java.util.List;

public class RestaurantCreateDTO {
    private String name;
    public RestaurantCreateDTO(){}
    public RestaurantCreateDTO(String name){
        this.name=name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
