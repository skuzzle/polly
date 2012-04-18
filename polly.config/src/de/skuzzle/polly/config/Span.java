package de.skuzzle.polly.config;


public class Span {

    private Position start;
	private Position end;
	
	
	public Span(Position start, Position end) {
	    this.start = start;
		this.end = end;
	}
	
	
	
	public Position getStart() {
        return this.start;
    }
	
	
	
	public Position getEnd() {
		return this.end;
	}
	
	
	
	public String substring(String original) {
	    return original.substring(this.start.getIndex(), this.end.getIndex());
	}
	
	
	
	public int getLength() {
		return this.end.getIndex() - this.start.getIndex();
	}

	
	
	@Override
	public String toString() {
	    return "[" + this.start.toString() + " - " + this.end.toString() + "]";
	}
}
