package polly.rx.core.orion;


public class OrionException extends Exception {

    private static final long serialVersionUID = 1L;

    public OrionException() {
        super();
    }

    public OrionException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public OrionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrionException(String message) {
        super(message);
    }

    public OrionException(Throwable cause) {
        super(cause);
    }
}
