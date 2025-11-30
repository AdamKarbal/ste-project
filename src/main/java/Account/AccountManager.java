package Account;

import Api.Utils.Requester;
import Restaurant.*;

public class AccountManager extends Account {

    // Le service stateless (outil de communication)
    private RestaurantManager restaurantManager;

    // Le nom du restaurant que ce compte possède
    private String myRestaurantName;

    public AccountManager(String name){
        super(name);
        // On initialise le service stateless ici
        this.restaurantManager = new RestaurantManager();
    }

    public void createRestaurant(String restaurantName, RestaurantFactory restaurantFactory){
        Restaurant newRestaurant = restaurantFactory.createRestaurant(restaurantName);
        try {
            Requester.createRestaurant(newRestaurant);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de créer le restaurant sur le serveur", e);
        }
        this.myRestaurantName = restaurantName;
    }

    public RestaurantManager getRestaurantManager() {
        return restaurantManager;
    }

    // Utile pour tes tests : permet de récupérer l'objet Restaurant courant depuis le serveur
    public Restaurant getRestaurant() {
        if (myRestaurantName == null) return null;
        try {
            // Il faut que tu rendes getRestaurantFromRepo PUBLIQUE dans RestaurantManager
            return restaurantManager.getRestaurantFromRepo(myRestaurantName);
        } catch (Exception e) {
            return null;
        }
    }
}