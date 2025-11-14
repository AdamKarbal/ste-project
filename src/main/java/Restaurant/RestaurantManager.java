package Restaurant;
import Dish.*;
import Dish.suggest.Suggestion;
import Restaurant.Enum.DayOfTheWeek;
import Restaurant.Enum.CuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.*;


import java.time.LocalTime;

public class RestaurantManager {
    //Instances
    private Restaurant restaurant;
    private final SmartDishFactory smartDishFactory = new SmartDishFactory();

    //Constructeur
    public RestaurantManager(Restaurant restaurant){
        this.restaurant= restaurant;
    }

    // Getter & setter
    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant){
        this.restaurant=restaurant;
    }

    //Méthodes
    // Pourquoi on fait comme ca ? parceque c'est au manager de demandé au restaurant l'état est en fonction il choisit quoi faire
    // c'est pas au restaurant de le faire même si il verifie.
    public void updateSchedule(DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) {
        RestaurantScheduleManager.updateSchedule(restaurant, day, start, end, capacity);
    }


    public void updateCapacity(DayOfTheWeek day, LocalTime start, LocalTime end, int capacity) throws ScheduleNoExist {
        RestaurantScheduleManager.updateCapacity(restaurant, day, start, end, capacity);
    }

    public void addDish(Dish d) throws DishAlreadyInDishes {
        if(restaurant.alreadyDish(d)) throw new DishAlreadyInDishes("Le plat "+ d+ " est déjà dans le restaurant." ) ;
        restaurant.addDish(d);
    }

    public void updateDish(Dish old, Dish replacement) throws dishNoInDishes {
        if(!(restaurant.alreadyDish(old))) throw new dishNoInDishes("Impossible de mettre à jour le plat, il n'est pas présent.");
        restaurant.removeDish(old);
        restaurant.addDish(replacement);
    }

    public void removeDish(Dish d) throws dishNoInDishes{
        if(!(restaurant.alreadyDish(d))) throw new dishNoInDishes("Impossible d'enlever le plat :" + d + ", il n'est pas présent dans le restaurant.");
    }

    public void addCuisineType(CuisineType c) throws CuisineTypeInList {
        if (restaurant.alreadyCuisineType(c))
            throw new CuisineTypeInList("Le type de cuisine " + c + " est déjà ajouté.");
        restaurant.addCuisineType(c);
    }

    public void removeCuisineType(CuisineType c) throws CuisineTypeNoExist {
        if (!(restaurant.alreadyCuisineType(c)))
            throw new CuisineTypeNoExist("Le type de cuisine " + c + " n'existe pas dans le restaurant.");
        restaurant.removeCuisineType(c);
    }


    public void addEstablishmentType(establishmentType e) throws EstablishmentTypeAlreadySet {
        if (restaurant.getEstablishmentType() != null)
            throw new EstablishmentTypeAlreadySet("Le restaurant a déjà un type d'établissement défini.");
        restaurant.setEstablishmentType(e);
    }

    public void removeEstablishmentType() throws EstablishmentTypeNoExist {
        if (restaurant.getEstablishmentType() == null)
            throw new EstablishmentTypeNoExist("Le restaurant n'a pas de type d'établissement défini.");
        restaurant.setEstablishmentType(null);
    }

    public DishTopping createToppingForDish(Dish dish, String name, float price) {
        DishTopping topping = new DishTopping(name, price);
        dish.addAvailableTopping(topping);
        return topping;
    }
    public Suggestion previewSuggestion(String nameOrKeyword) {
        return smartDishFactory.suggest(nameOrKeyword);
    }

    public Dish createDish(DishInput input) throws DishAlreadyInDishes {
        Dish dish = smartDishFactory.create(input);
        this.addDish(dish); // tes règles (doublon, etc.)
        return dish;
    }

}
