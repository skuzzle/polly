package de.skuzzle.polly.sdk.util;


public class CompareException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompareException() {
        super();
    }

    public CompareException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CompareException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompareException(String message) {
        super(message);
    }

    public CompareException(Throwable cause) {
        super(cause);
    }
}
