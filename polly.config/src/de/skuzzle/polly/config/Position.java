package de.skuzzle.polly.config;


public class Position {

	
	private int line;
	private int column;
	private int index;
	
	
	
	public Position(int line, int colum, int index) {
		this.line = line;
		this.column = colum;
		this.index = index;
	}
	
	
	
	public Position(Position other) {
		this(other.getLine(), other.getColumn(), other.getIndex());
	}
	
	
	
	int getChar(char[] stream) {
		if (this.index < stream.length) {
			return stream[this.index];
		} else {
			return -1;
		}
	}
	
	
	
	void incrementColumn() {
		++this.column;	
		++this.index;
	}
	
	
	
	void incrementLine() {
		this.column = 1;
		++this.line;
		++this.index;
	}
	
	
	
	void set(int line, int column, int index) {
		this.line = line;
		this.column = column;
		this.index = index;
	}
	
	
	
	public int getLine() {
		return this.line;
	}
	
	
	
	public int getColumn() {
		return this.column;
	}
	
	
	
	public int getIndex() {
		return this.index;
	}
	
	
	@Override
	public String toString() {
		return "[Line: " + this.line + ", Column: " + this.column + "]";
	}
}