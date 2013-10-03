package de.skuzzle.polly.sdk.httpv2;


public class SuccessResult {
    public final boolean success;
    public final String message;
    public SuccessResult(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }
}