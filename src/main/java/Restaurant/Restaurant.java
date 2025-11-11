package Restaurant;
import Dish.Dish;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.dishNoInDishes;
import Restaurant.Enum.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Restaurant {

    // Instances
    private String name;
    private List<Dish> dishes;
    private List<RestaurantSchedule> restaurantSchedules;
    private List<CuisineType> CuisineTypes;
    private establishmentType establishmentType;
    //Constructeur

    public Restaurant(Builder builder){
        this.name = builder.name;
        this.dishes = builder.dishes;
        this.restaurantSchedules = builder.restaurantSchedules;
        this.CuisineTypes = builder.CuisineTypes;
        this.establishmentType = builder.establishmentType;
    }
    // Getter & setter


    public String getName() {
        return name;
    }

    public boolean containsDish(Dish dish){
        for (Dish d : dishes){
            if (d.equals(dish)){
                return true;
            }
        }
        return false;
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


    public List<CuisineType> getCuisineTypes() {
        return CuisineTypes;
    }

    public void setCuisineTypes(List<CuisineType> CuisineTypes) {
        this.CuisineTypes = CuisineTypes;
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
        return dishes.contains(dish);
    }
    public void addSchedule(RestaurantSchedule schedule){
        this.restaurantSchedules.add(schedule);
    }

    public boolean alreadyScheduled(LocalTime start, LocalTime end, DayOfTheWeek day) {
        if (restaurantSchedules == null || restaurantSchedules.isEmpty()) {
            return false;
        }

        return restaurantSchedules.stream()
                .anyMatch(s ->
                        s.getDay() == day &&
                                s.getStart().equals(start) &&
                                s.getEnd().equals(end)
                );
    }

    public void setNewCapacity(LocalTime start, LocalTime end, DayOfTheWeek day, int capacity) {
        if (restaurantSchedules == null || restaurantSchedules.isEmpty()) {
            return;
        }

        restaurantSchedules.stream()
                .filter(s ->
                        s.getDay() == day &&
                                !s.getStart().isBefore(start) &&  // s >= start
                                s.getStart().isBefore(end)        // s < end
                )
                .forEach(s -> s.setCapacity(capacity));
    }

    public void addCuisineType(CuisineType c) {
        CuisineTypes.add(c);
    }

    public void removeCuisineType(CuisineType c) {
        CuisineTypes.remove(c);
    }

    public boolean alreadyCuisineType(CuisineType c) {
        return CuisineTypes.contains(c);
    }

    public Dish findDish(String name, double price) {
        Dish target = new Dish(price, name, "", null, null, null);
        return dishes.stream()
                .filter(d -> d.equals(target))
                .findFirst()
                .orElse(null);
    }




    /*@Override
    public String toString(){
        return name + " " + establishmentType + "\n.";
    }*/

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
    @Override
    public String toString(){
        return "Nom : "+name+"plats :"+dishes+"schedules : "+restaurantSchedules+"typeCuisine"+getCuisineTypes();
    }

    public boolean isOpenNow() {
        return RestaurantScheduleManager.isOpenNow(this);
    }

    public boolean isOpenAt(LocalDateTime dateTime) {
        return RestaurantScheduleManager.isOpenAt(this, dateTime);
    }


    // InnerClass builder pour éviter d'avoir à faire plusieurs constructeurs en fonction du nombre de paramètre qu'on entre

    public static class Builder {
        private String name;
        private List<Dish> dishes;
        private List<RestaurantSchedule> restaurantSchedules;
        private List<CuisineType> CuisineTypes;
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

        public Builder cuisineType(List<CuisineType> CuisineTypes){
            this.CuisineTypes = CuisineTypes;
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
