package Restaurant.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum establishmentType {
    RESTAURANT("Restaurant"),
    FAST_FOOD("Fast Food"),
    CAFE("Café"),
    PIZZERIA("Pizzeria"),
    SUSHI_BAR("Sushi Bar");

    private final String label;

    establishmentType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}
