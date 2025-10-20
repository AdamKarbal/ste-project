package Dish;

import Restaurant.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class Dish {
    private double price;
    private String name;
    private String description;
    private DishCategory category;
    private DishType type;
    private List<DishTag> dishTag;
    private List<DishTopping> toppings;

    public Dish(double price, String name, String description, DishCategory category, DishType type,List<DishTag> dishTag) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.category = category;
        this.type = type;
        this.dishTag = dishTag;

        this.toppings = new ArrayList<>();
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public DishCategory getCategory() {
        return category;
    }
    public void setCategory(DishCategory category) {
        this.category = category;
    }
    public DishType getType() {
        return type;
    }

    public void setType(DishType type) {
        this.type = type;
    }
    public List<DishTag> getDishTag() {
        return dishTag;
    }
    public void setDishTag(List<DishTag> dishTag) {
        this.dishTag = dishTag;
    }
    public boolean addDishTag(DishTag dishTag) {
        return this.dishTag.add(dishTag);
    }

    public List<DishTopping> getToppings() {
        return toppings;
    }

    public void addTopping(DishTopping topping) {
        if(!toppings.contains(topping)){
            this.toppings.add(topping);
        }
    }

    public void setToppings(List<DishTopping> toppings) {
        this.toppings = toppings;
    }
    public void removeTopping(DishTopping topping) {
        this.toppings.remove(topping);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish)) return false;
        Dish other = (Dish) o;
        double delta = 1e-6;
        return name != null && other.name != null
                && name.equalsIgnoreCase(other.name) && Math.abs(price - other.price) < delta;
    }
    @Override
    public int hashCode() {
        return name == null ? 0 : name.toLowerCase().hashCode();
    }

}
