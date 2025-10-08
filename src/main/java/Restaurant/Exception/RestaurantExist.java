package Restaurant.Exception;

public class RestaurantExist extends RuntimeException {
    public RestaurantExist(String message) {
        super(message);
    }
}
