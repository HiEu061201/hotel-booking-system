package fu.hbs.exceptionHandler;

public class SearchRoomCustomerExceptionHandler extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public SearchRoomCustomerExceptionHandler(String message) {
        super(message);
    }
}
