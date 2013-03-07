package de.skuzzle.polly.sdk.exceptions;


public class EMailException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public EMailException() {
        super();
    }

    public EMailException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EMailException(String message, Throwable cause) {
        super(message, cause);
    }

    public EMailException(String message) {
        super(message);
    }

    public EMailException(Throwable cause) {
        super(cause);
    }


    
}
