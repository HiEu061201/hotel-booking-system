package fu.hbs.exceptionHandler;

public class CancellationExistException extends Exception {
    private static final long serialVersionUID = 1L;

    public CancellationExistException(String message) {
        super(message);
    }
}
