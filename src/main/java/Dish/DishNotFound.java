package Dish;

public class DishNotFound extends RuntimeException {
    public DishNotFound(String message) {
        super(message);
    }
}
