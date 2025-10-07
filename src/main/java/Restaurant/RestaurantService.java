package Restaurant;

public class RestaurantService {
    private static RestaurantRepo repo;

    public RestaurantService(RestaurantRepo r){
        repo = r;
    }

    public static void addRestaurant(Restaurant restaurant) throws RestaurantExist {
        if (repo.restaurantExist(restaurant)) {
            throw new RestaurantExist("Le restaurant existe déjà dans le repo");
        }
        repo.addRestaurant(restaurant);
    }
}
