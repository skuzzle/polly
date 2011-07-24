package de.skuzzle.polly.sdk.exceptions;

public class PluginException extends Exception {

    private static final long serialVersionUID = 1L;

    public PluginException() {
        super();
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }
}
