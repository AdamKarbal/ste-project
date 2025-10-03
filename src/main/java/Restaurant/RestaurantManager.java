package Restaurant;
import Utils.*;

public class RestaurantManager {
    //Instances
    private Restaurant restaurant;

    //Constructeur
    public RestaurantManager(Restaurant restaurant){
        this.restaurant=restaurant;
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

    public void addDish(){}

    public void updateDish(){}

    public void removeDish(){}

    public void addCuisineType(){}

    public void removeCuisineType(){}

    public void addEstablishmentType(){}

    public void removeEstablishmentType(){}
}
