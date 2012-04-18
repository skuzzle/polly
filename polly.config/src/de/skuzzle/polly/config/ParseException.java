package de.skuzzle.polly.config;


public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	private Span position;
	
	
	public ParseException(String message, Span position) {
		super(message + " " + position);
		this.position = position;
	}
	
	
	
	public Span getPosition() {
		return this.position;
	}
}
