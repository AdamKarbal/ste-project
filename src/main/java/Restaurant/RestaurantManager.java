package Restaurant;
import Dish.*;
import Restaurant.Enum.cuisineType;
import Restaurant.Enum.establishmentType;
import Restaurant.Exception.*;
import Utils.*;

import java.util.ArrayList;

public class RestaurantManager {
    //Instances
    private Restaurant restaurant;

    //Constructeur
    public RestaurantManager(String name){
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(name)
                .dishes(new ArrayList<>())
                .restaurantSchedules(new ArrayList<>())
                .cuisineType(new ArrayList<>())
                .establishmentType(null);
        this.restaurant = builder.build();
        try{RestaurantService.addRestaurant(restaurant);}catch (RestaurantExist e){
            // ici faire en sorte qu'on récupère un nv nom ? avec un scanner ou autre ?
        }

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
    public void updateSchedule(Date start, Date end, int capacity ){
        if(restaurant.alreadyScheduled(start, end)){
            restaurant.setNewCapacity(start,end,capacity);
        }
        else{
            RestaurantSchedule newRS = new RestaurantSchedule(start,end,capacity);
            restaurant.addSchedule(newRS);
        }
    }

    public void updateCapacity(Date start, Date end, int capacity) throws ScheduleNoExist{
        if(!(restaurant.alreadyScheduled(start,end))){
            throw new ScheduleNoExist("Le schedule à modifé demandé n'existe pas");
        }
        restaurant.setNewCapacity(start,end,capacity);
    }

    public void addDish(Dish d) throws DishInDishes {
        if(restaurant.alreadyDish(d)) throw new DishInDishes("Le plat "+ d+ " est déjà dans le restaurant." ) ;
        restaurant.addDish(d);
    }

    public void updateDish(Dish old, Dish young) throws dishNoInDishes {
        if(!(restaurant.alreadyDish(old))) throw new dishNoInDishes("Impossible de mettre à jour le plat, il n'est pas présent.");
        restaurant.removeDish(old);
        restaurant.addDish(young);
    }

    public void removeDish(Dish d) throws dishNoInDishes{
        if(!(restaurant.alreadyDish(d))) throw new dishNoInDishes("Impossible d'enlever le plat :" + d + ", il n'est pas présent dans le restaurant.");
    }

    public void addCuisineType(cuisineType c) throws CuisineTypeInList {
        if (restaurant.alreadyCuisineType(c))
            throw new CuisineTypeInList("Le type de cuisine " + c + " est déjà ajouté.");
        restaurant.addCuisineType(c);
    }

    public void removeCuisineType(cuisineType c) throws CuisineTypeNoExist {
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
}
