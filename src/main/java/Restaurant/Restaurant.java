package Restaurant;
import Dish.Dish;
import Restaurant.Enum.cuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.dishNoInDishes;
import Utils.*;
import Restaurant.Enum.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Restaurant {

    // Instances
    private String name;
    private List<Dish> dishes;
    private List<RestaurantSchedule> restaurantSchedules;
    private List<cuisineType> cuisineTypes;
    private establishmentType establishmentType;

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
                                s.getStart().equals(start) &&
                                s.getEnd().equals(end)
                )
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
        return isOpenAt(LocalDateTime.now());
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


    public boolean isOpenAt(LocalDateTime dateTime) {
        if (restaurantSchedules == null || restaurantSchedules.isEmpty()) return false;

        DayOfTheWeek currentDay = convertDayOfWeek(dateTime.getDayOfWeek());
        LocalTime time = dateTime.toLocalTime();

        return restaurantSchedules.stream().anyMatch(s -> {
            DayOfTheWeek scheduleDay = s.getDay();
            boolean overnight = s.getEnd().isBefore(s.getStart());

            // Cas normal : même jour
            if (!overnight && scheduleDay == currentDay) {
                return !time.isBefore(s.getStart()) && time.isBefore(s.getEnd());
            }

            // Cas traversant minuit : 22h → 2h du matin
            if (overnight) {
                // ouvert le jour du début (22h–00h)
                if (scheduleDay == currentDay && !time.isBefore(s.getStart())) return true;
                // ouvert le jour suivant (00h–2h)
                if (nextDay(scheduleDay) == currentDay && time.isBefore(s.getEnd())) return true;
            }

            return false;
        });
    }

    private DayOfTheWeek convertDayOfWeek(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> DayOfTheWeek.MONDAY;
            case TUESDAY -> DayOfTheWeek.TUESDAY;
            case WEDNESDAY -> DayOfTheWeek.WEDNESDAY;
            case THURSDAY -> DayOfTheWeek.THURSDAY;
            case FRIDAY -> DayOfTheWeek.FRIDAY;
            case SATURDAY -> DayOfTheWeek.SATURDAY;
            case SUNDAY -> DayOfTheWeek.SUNDAY;
        };
    }

    private DayOfTheWeek nextDay(DayOfTheWeek day) {
        return switch (day) {
            case MONDAY -> DayOfTheWeek.TUESDAY;
            case TUESDAY -> DayOfTheWeek.WEDNESDAY;
            case WEDNESDAY -> DayOfTheWeek.THURSDAY;
            case THURSDAY -> DayOfTheWeek.FRIDAY;
            case FRIDAY -> DayOfTheWeek.SATURDAY;
            case SATURDAY -> DayOfTheWeek.SUNDAY;
            case SUNDAY -> DayOfTheWeek.MONDAY;
            default -> day;
        };
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
