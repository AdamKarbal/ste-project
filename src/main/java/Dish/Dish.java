package Dish;

import Restaurant.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class Dish {
    private float price;
    private String name;
    private String description;
    private DishCategory category;
    private DishType type;
    private DishTag dishTag;
    private List<DishTopping> toppings;
    private Restaurant restaurant;

    public Dish(float price, String name, String description, DishCategory category, DishType type, Restaurant restaurant, DishTag dishTag) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.category = category;
        this.type = type;
        this.dishTag = dishTag;
        this.restaurant = restaurant;
        this.toppings = new ArrayList<>();
    }

    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
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
    public Restaurant getRestaurant() {
        return restaurant;

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
    public DishTag getDishTag() {
        return dishTag;
    }
    public void setDishTag(DishTag dishTag) {
        this.dishTag = dishTag;
    }
    public List<DishTopping> getToppings() {
        return toppings;
    }

    public void addTopping(DishTopping topping) {
        if(!toppings.contains(topping)){
            this.toppings.add(topping);
        }
    }
    public void removeTopping(DishTopping topping) {
        this.toppings.remove(topping);
    }


}
