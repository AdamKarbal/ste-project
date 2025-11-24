package Account;

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
        // 1. Création de l'objet Restaurant via la Factory
        Restaurant newRestaurant = restaurantFactory.createRestaurant(restaurantName);

        // 2. Sauvegarde sur le serveur via le Manager (PUT)
        // ATTENTION : Assure-toi que la méthode saveRestaurant est 'public' dans RestaurantManager !
        try {
            // Il faut que tu rendes la méthode saveRestaurant PUBLIQUE dans RestaurantManager
            // Ou que tu passes par une méthode publique qui le fait.
            // Pour l'instant, supposons que tu as rendu saveRestaurant public :
            restaurantManager.saveRestaurant(newRestaurant);

        } catch (Exception e) {
            throw new RuntimeException("Impossible de créer le restaurant sur le serveur", e);
        }

        // 3. On mémorise le nom
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