package de.skuzzle.polly.sdk.exceptions;


public class ConversationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConversationException() {
        super();
    }

    public ConversationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversationException(String message) {
        super(message);
    }

    public ConversationException(Throwable cause) {
        super(cause);
    }

}
