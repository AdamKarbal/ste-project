package Dish;

public enum DishTag {
    GLUTEN_FREE("Gluten-free"),
    CONTAINS_FROZEN_PRODUCTS("Contains frozen products"),
    MAY_CONTAIN_TRACES_OF_PEANUTS("May contain traces of peanuts"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    HALAL("Halal"),
    KOSHER("Kosher"),
    LACTOSE_FREE("Lactose-free"),
    ORGANIC("Organic"),
    SPICY("Spicy");

    private final String label;

    DishTag(String label) {
;        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
