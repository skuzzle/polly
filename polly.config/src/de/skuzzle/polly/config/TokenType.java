package de.skuzzle.polly.config;


enum TokenType {
	IDENTIFIER("Identifier", false), 
	STRING("String", false),
	BLOCKCOMMENT("Block-Comment", false),
	INLINECOMMENT("Inline-Comment", false),
	COMMA(",", false), 
	EQ("=", false), 
	OPENSQBR("[", false), 
	CLOSEDSQBR("]", false), 
	OPENCBR("{", false), 
	CLOSEDCBR("}", false),
	TRUE("true", false),
	FALSE("false", false),
	NUMBER("Number", false),
	LINEBREAK("Linebreak", true),
	INCLUDE("Include", false),
	ANY("Any", false),
	EOS("#", false), 
	VALUE("Value", false);
	
	
	private String string;
	private boolean belongsToPrevious;
	
	private TokenType(String string, boolean belongsToPrevious) {
		this.string = string;
		this.belongsToPrevious = belongsToPrevious;
	}
	
	
	
	public boolean belongsToPrevious() {
	    return this.belongsToPrevious;
	}
	
	
	@Override
	public String toString() {
		return this.string;
	}
}