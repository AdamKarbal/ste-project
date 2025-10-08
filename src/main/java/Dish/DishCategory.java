package Dish;

public enum DishCategory {
    STARTER("Starter"),
    MAIN_COURSE("Main Course"),
    DESSERT("Dessert"),
    DRINK("Drink");

    private final String label;

    DishCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

