package polly.core;


public class SetupException extends Exception {

    private static final long serialVersionUID = 1L;

    public SetupException() {
        super();
    }

    public SetupException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetupException(String message) {
        super(message);
    }

    public SetupException(Throwable cause) {
        super(cause);
    }

    
}
