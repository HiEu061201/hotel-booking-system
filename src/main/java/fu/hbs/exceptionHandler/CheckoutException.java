package fu.hbs.exceptionHandler;

public class CheckoutException extends RuntimeException{
    public CheckoutException(String message) {
        super(message);
    }
}