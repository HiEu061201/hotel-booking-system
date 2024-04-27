package fu.hbs.exceptionHandler;

public class NotFoundBooking extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NotFoundBooking(String message) {
        super(message);

    }
}
