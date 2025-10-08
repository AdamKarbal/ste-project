package Dish;

public class DishTopping {
    private final String name;
    private final float price;

    public DishTopping(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " (+ " + price + "€)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DishTopping)) return false;
        DishTopping other = (DishTopping) obj;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
