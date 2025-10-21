package Dish;


import java.util.ArrayList;
import java.util.List;

public class Dish {
    private double price;
    private String name;
    private String description;
    private DishCategory category;
    private DishType type;
    private List<DishTag> dishTag;
    private List<DishTopping> availableToppings;
    private List<DishTopping> selectedToppings;

    public Dish(double price, String name, String description, DishCategory category, DishType type,List<DishTag> dishTag) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.category = category;
        this.type = type;
        this.dishTag = dishTag;

        this.availableToppings = new ArrayList<>();
        this.selectedToppings = new ArrayList<>();
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

    public List<DishTopping> getAvailableToppings() {
        return new ArrayList<>(availableToppings);
    }

    public void addAvailableTopping(DishTopping topping) {
        if (!availableToppings.contains(topping)) {
            availableToppings.add(topping);
        }
    }

    public void removeAvailableTopping(DishTopping topping) {
        availableToppings.remove(topping);
    }


    public void addSelectedTopping(DishTopping topping) {
        if (availableToppings.contains(topping) && !selectedToppings.contains(topping)) {
            selectedToppings.add(topping);
        } else {
            throw new IllegalArgumentException("Ce topping n'est pas disponible pour ce plat !");
        }
    }

    public List<DishTopping> getSelectedToppings() {
        return new ArrayList<>(selectedToppings);
    }

    public void removeSelectedTopping(DishTopping topping) {
        selectedToppings.remove(topping);
    }

    public double getToppingTotalPrice() {
        double total = price;
        for (DishTopping t : selectedToppings) {
            total += t.getPrice();
        }
        return total;
    }




}
