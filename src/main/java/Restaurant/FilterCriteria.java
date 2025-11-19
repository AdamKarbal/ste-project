package Restaurant;

import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;

import java.time.LocalTime;
import java.util.*;

public class FilterCriteria {
    private String restaurantName;
    private Double priceMin;
    private Double priceMax;
    private List<establishmentType> establishments;
    private List<CuisineType> cuisines;
    private LocalTime availableAt;
    private Boolean openNow;
    private Set<DishTag> tagsAllOf;
    private Set<DishTag> tagsAnyOf;

    private FilterCriteria() {}

    public static Builder builder() {
        return new Builder();
    }

    // --- Getters ---
    public String getNameContains() { return restaurantName; }
    public List<CuisineType> getCuisines() { return cuisines; }
    public List<establishmentType> getEstablishments() { return establishments; }
    public Double getPriceMin() { return priceMin; }
    public Double getPriceMax() { return priceMax; }
    public LocalTime getOpenAt() { return availableAt; }
    public Boolean getOpenNow() { return openNow; }
    public Set<DishTag> getTagsAllOf() { return tagsAllOf; }
    public Set<DishTag> getTagsAnyOf() { return tagsAnyOf; }

    // --- Builder ---
    public static class Builder {
        private final FilterCriteria c = new FilterCriteria();

        public Builder nameContains(String s) {
            c.restaurantName = emptyToNull(s);
            return this;
        }

        public Builder cuisine(CuisineType ct) {
            if (c.cuisines == null) c.cuisines = new ArrayList<>();
            c.cuisines.add(ct);
            return this;
        }

        public Builder establishment(establishmentType et) {
            if (c.establishments == null) c.establishments = new ArrayList<>();
            c.establishments.add(et);
            return this;
        }

        public Builder priceMin(Double v) {
            c.priceMin = v;
            return this;
        }

        public Builder priceMax(Double v) {
            c.priceMax = v;
            return this;
        }

        public Builder availableAt(LocalTime t) {
            c.availableAt = t;
            return this;
        }

        public Builder openNow(Boolean b) {
            c.openNow = b;
            return this;
        }

        public Builder tagsAllOf(Collection<DishTag> tags) {
            c.tagsAllOf = toSet(tags);
            return this;
        }

        public Builder tagsAnyOf(Collection<DishTag> tags) {
            c.tagsAnyOf = toSet(tags);
            return this;
        }

        public FilterCriteria build() {
            return c;
        }

        private static String emptyToNull(String s) {
            return (s == null || s.isBlank()) ? null : s;
        }

        private static Set<DishTag> toSet(Collection<DishTag> in) {
            return (in == null) ? null : new HashSet<>(in);
        }
    }
}
