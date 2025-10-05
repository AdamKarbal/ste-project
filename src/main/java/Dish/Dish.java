package Dish;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private double price;
    private List<DishTag> tags;

    public Dish() {
        tags = new ArrayList<DishTag>();
    }

    public List<DishTag> getTags() {
        return tags;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double v) {
        this.price = v;
    }
}
