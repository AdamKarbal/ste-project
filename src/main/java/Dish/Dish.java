package Dish;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    List<DishTag> tags;

    public Dish() {
        tags = new ArrayList<DishTag>();
    }

    public List<DishTag> getTags() {
        return tags;
    }
}
