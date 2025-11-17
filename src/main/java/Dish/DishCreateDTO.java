package Dish;

public class DishCreateDTO {

    private double price;
    private String name;
    private String description;
    private DishCategory category; // ou Category si tu as un enum
    private DishType type;     // ou Type si tu as un enum

    public DishCreateDTO() {}

    public DishCreateDTO(double price, String name, String description, DishCategory category, DishType type) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.category = category;
        this.type = type;
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
}
