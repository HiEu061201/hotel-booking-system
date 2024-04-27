package fu.hbs.exceptionHandler;

public class CheckInException extends RuntimeException{
    public CheckInException(String message) {
        super(message);
    }
}