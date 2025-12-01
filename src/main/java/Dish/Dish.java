package Dish;


import Restaurant.Restaurant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dish {
    private double price;
    //private static int compteur=0;
    private String name;
    private String description;
    private DishCategory category;
    private DishType type;
    private List<DishTag> dishTag = new ArrayList<>();
    private List<DishTopping> availableToppings = new ArrayList<>();
    private List<DishTopping> selectedToppings = new ArrayList<>();
    private int id;

    public Dish() {
    }
    public Dish(double price, String name, String description, DishCategory category, DishType type,List<DishTag> dishTag) {
        this.id=UUID.randomUUID().hashCode();
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
    public int getId(){
        return id;
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
    @JsonIgnore
    public double getToppingTotalPrice() {
        double total = price;
        for (DishTopping t : selectedToppings) {
            total += t.getPrice();
        }
        return total;
    }

    @Override
    public String toString(){
        return name+id+"";
    }

    private static String norm(String s) {
        if (s == null) return null;
        String noAccents = Normalizer.normalize(s.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", ""); // supprime accents
        return noAccents.replaceAll("\\s+", " ")                 // espaces multiples -> simple
                .toUpperCase(Locale.ROOT);               // insensible à la casse
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish other)) return false;

        String n1 = norm(this.name);
        String n2 = norm(other.name);

        // Si l’un des deux n’a pas de nom, on ne les considère pas égaux
        if (n1 == null || n2 == null) return false;

        return n1.equals(n2);
    }

    @Override
    public int hashCode() {
        String n = norm(this.name);
        return (n == null) ? 0 : n.hashCode();
    }

    public DishCreateDTO toCreateDTO() {
        return new DishCreateDTO(
                this.id,
                this.price,
                this.name,
                this.description,
                this.category,
                this.type
        );
    }



}
