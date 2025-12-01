package Dish;

public enum DishTag {
    // ---- Préférences alimentaires / style de vie ----
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    HALAL("Halal"),
    KOSHER("Kosher"),
    CONTAINS_FROZEN_PRODUCTS("CONTAINS_FROZEN_PRODUCTS"),
    // ---- Profil nutritionnel / santé ----
    ORGANIC("Organic"),
    LOW_CALORIE("Low calorie"),
    HIGH_PROTEIN("High protein"),
    NO_ADDED_SUGAR("No added sugar"),
    SUGAR_FREE("Sugar-free"),

    // ---- Sans / tolérances ----
    LACTOSE_FREE("Lactose-free"),
    GLUTEN_FREE("Gluten-free"),

    // ---- Profil de goût ----
    MILD("Mild"),
    SPICY("Spicy"),
    GARLIC_RICH("Garlic-rich"),
    CHEESY("Cheesy"),
    SMOKED("Smoked"),

    // ---- Qualité / provenance ----
    HOMEMADE("Homemade"),
    ARTISANAL("Artisanal"),
    LOCALLY_SOURCED("Locally sourced"),
    NO_ADDITIVES("No additives"),
    NO_PRESERVATIVES("No preservatives"),

    // ---- Allergènes : contient ----
    CONTAINS_PEANUTS("Contains peanuts"),
    CONTAINS_NUTS("Contains nuts"),
    CONTAINS_EGGS("Contains eggs"),
    CONTAINS_MILK("Contains milk"),
    CONTAINS_SOY("Contains soy"),
    CONTAINS_FISH("Contains fish"),
    CONTAINS_SHELLFISH("Contains shellfish"),
    CONTAINS_SESAME("Contains sesame"),

    // ---- Allergènes : peut contenir des traces ----
    MAY_CONTAIN_TRACES_OF_PEANUTS("May contain traces of peanuts"),
    MAY_CONTAIN_TRACES_OF_NUTS("May contain traces of nuts"),
    MAY_CONTAIN_TRACES_OF_EGGS("May contain traces of eggs"),
    MAY_CONTAIN_TRACES_OF_MILK("May contain traces of milk"),
    MAY_CONTAIN_TRACES_OF_SOY("May contain traces of soy"),
    MAY_CONTAIN_TRACES_OF_FISH("May contain traces of fish"),
    MAY_CONTAIN_TRACES_OF_SHELLFISH("May contain traces of shellfish"),
    MAY_CONTAIN_TRACES_OF_SESAME("May contain traces of sesame");

    private final String label;

    DishTag(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
