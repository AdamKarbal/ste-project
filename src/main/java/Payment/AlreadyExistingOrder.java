package Payment;

public class AlreadyExistingOrder extends RuntimeException {
    public AlreadyExistingOrder(String message) {
        super(message);
    }
}
