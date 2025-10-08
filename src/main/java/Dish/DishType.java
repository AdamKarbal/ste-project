package Dish;

public enum DishType {
    PASTA("Pasta"),
    MEAT("Meat"),
    PIZZA("Pizza"),
    ICE_CREAM("Ice Cream"),
    CAKE("Cake"),
    SALAD("Salad"),
    SOUP("Soup"),
    SEAFOOD("Seafood"),
    SANDWICH("Sandwich"),
    OTHER("Other");

    private final String label;

    DishType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
