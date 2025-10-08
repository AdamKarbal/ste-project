package Restaurant;
import Dish.Dish;
import Restaurant.Enum.cuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.dishNoInDishes;
import Utils.*;

import java.util.List;

public class Restaurant {

    // Instances
    private String name;
    private List<Dish> dishes;
    private List<RestaurantSchedule> restaurantSchedules;
    private List<cuisineType> cuisineTypes;
    private Restaurant.Enum.establishmentType establishmentType;

    //Constructeur

    public Restaurant(Builder builder){
        this.name = builder.name;
        this.dishes = builder.dishes;
        this.restaurantSchedules = builder.restaurantSchedules;
        this.cuisineTypes = builder.cuisineTypes;
        this.establishmentType = builder.establishmentType;
    }
    // Getter & setter


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<RestaurantSchedule> getRestaurantSchedules() {
        return restaurantSchedules;
    }

    public void setRestaurantSchedules(List<RestaurantSchedule> restaurantSchedules) {
        this.restaurantSchedules = restaurantSchedules;
    }

    public List<cuisineType> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(List<cuisineType> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }

    public establishmentType getEstablishmentType() {
        return establishmentType;
    }

    public void setEstablishmentType(establishmentType establishmentType) {
        this.establishmentType = establishmentType;
    }

    // Méthodes

    public void addDish(Dish dish){
        dishes.add(dish);
    }

    public void removeDish(Dish dish) throws dishNoInDishes {
        if(!(dishes.contains(dish))) throw new dishNoInDishes("Le plat : " + dish + "est déjà pas dans le restaurant");
        dishes.remove(dish);
    }

    public boolean alreadyDish(Dish dish){
        if(dishes.contains(dish)) return true;
        return false;
    }
    public void addSchedule(RestaurantSchedule schedule){
        this.restaurantSchedules.add(schedule);
    }

    public boolean alreadyScheduled(Date start, Date end){
        if(restaurantSchedules.isEmpty()){
            return false;
        }
        return restaurantSchedules.stream().anyMatch(s->s.getStart().equals(start) && s.getEnd().equals(end));
    }

    public void setNewCapacity(Date start, Date end, int capacity){
        restaurantSchedules.stream()
                .filter(s -> s.getStart().equals(start) && s.getEnd().equals(end))
                .forEach(s -> s.setCapacity(capacity));
    }

    public void addCuisineType(cuisineType c) {
        cuisineTypes.add(c);
    }

    public void removeCuisineType(cuisineType c) {
        cuisineTypes.remove(c);
    }

    public boolean alreadyCuisineType(cuisineType c) {
        return cuisineTypes.contains(c);
    }




    @Override
    public String toString(){
        return name + " " + establishmentType + "\n.";
    }

    public boolean isOpenNow() {
        Date now = Date.now();      // ← au lieu de new Date()
        return isOpenAt(now);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Restaurant other = (Restaurant) obj;
        return name != null && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }


    public boolean isOpenAt(Date dt) {
        if (restaurantSchedules == null || restaurantSchedules.isEmpty()) return false;
        return restaurantSchedules.stream()
                .anyMatch(s -> isWithin(dt, s.getStart(), s.getEnd()));
    }

    private static boolean isWithin(Date dt, Date start, Date end) {
        boolean overnight = end.isBefore(start);

        if (overnight) {
            return !dt.isBefore(start) || dt.isBefore(end); //
        } else {
            return !dt.isBefore(start) && dt.isBefore(end);
        }
    }



    // InnerClass builder pour éviter d'avoir à faire plusieurs constructeurs en fonction du nombre de paramètre qu'on entre

    public static class Builder {
        private String name;
        private List<Dish> dishes;
        private List<RestaurantSchedule> restaurantSchedules;
        private List<cuisineType> cuisineTypes;
        private establishmentType establishmentType;

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder dishes(List<Dish> dishes){
            this.dishes = dishes;
            return this;
        }

        public Builder restaurantSchedules(List<RestaurantSchedule> restaurantSchedules){
            this.restaurantSchedules=restaurantSchedules;
            return this;
        }

        public Builder cuisineType(List<cuisineType> cuisineTypes){
            this.cuisineTypes = cuisineTypes;
            return this;
        }

        public Builder establishmentType(establishmentType establishmentType){
            this.establishmentType=establishmentType;
            return this;
        }

        public Restaurant build(){
            return new Restaurant(this);
        }


    }
}
