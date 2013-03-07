package de.skuzzle.polly.sdk.exceptions;

public class PasteException extends Exception {

	private static final long serialVersionUID = 1L;

	public PasteException() {
		super();
	}

	public PasteException(String message, Throwable e) {
		super(message, e);
	}

	public PasteException(String message) {
		super(message);
	}

	public PasteException(Throwable e) {
		super(e);
	}

}
