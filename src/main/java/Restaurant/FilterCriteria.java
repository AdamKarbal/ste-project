package Restaurant;

import Dish.DishTag;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FilterCriteria {
    String restaurantName;
    Double priceMin;
    Double priceMax;
    establishmentType establishment;
    CuisineType cuisine;
    LocalTime availableAt;
    Boolean openNow;
    Set<DishTag> tagsAllOf;
    Set<DishTag> tagsAnyOf;

    private FilterCriteria() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getNameContains() { return restaurantName; }
    public CuisineType getCuisine() { return cuisine; }
    public establishmentType getEstablishment() { return establishment; }
    public Double getPriceMin() { return priceMin; }
    public Double getPriceMax() { return priceMax; }
    public LocalTime getOpenAt() { return availableAt; }
    public Boolean getOpenNow() { return openNow; }
    public Set<DishTag> getTagsAllOf() { return tagsAllOf; }
    public Set<DishTag> getTagsAnyOf() { return tagsAnyOf; }

    public static class Builder {
        private final FilterCriteria c = new FilterCriteria();

        public Builder nameContains(String s) { c.restaurantName = emptyToNull(s); return this; }
        public Builder cuisine(CuisineType ct) { c.cuisine = ct; return this; }
        public Builder establishment(establishmentType et) { c.establishment = et; return this; }
        public Builder priceMin(Double v) { c.priceMin = v; return this; }
        public Builder priceMax(Double v) { c.priceMax = v; return this; }
        public Builder availableAt(LocalTime t) { c.availableAt = t; return this; }
        public Builder openNow(Boolean b) { c.openNow = b; return this; }
        public Builder tagsAllOf(Collection<DishTag> tags) { c.tagsAllOf = toSet(tags); return this; }
        public Builder tagsAnyOf(Collection<DishTag> tags) { c.tagsAnyOf = toSet(tags); return this; }

        public FilterCriteria build() { return c; }

        private static String emptyToNull(String s){ return (s==null||s.isBlank())?null:s; }
        private static Set<DishTag> toSet(Collection<DishTag> in){
            return (in==null)?null:new HashSet<>(in);
        }
    }
}
