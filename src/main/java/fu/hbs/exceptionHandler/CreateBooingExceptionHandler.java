package fu.hbs.exceptionHandler;

public class CreateBooingExceptionHandler extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CreateBooingExceptionHandler(String message) {
        super(message);
    }
}
