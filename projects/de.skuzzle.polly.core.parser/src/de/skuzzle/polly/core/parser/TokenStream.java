package de.skuzzle.polly.core.parser;

public interface TokenStream {

    /**
     * Consumes the next token only if it has the expected type.
     * 
     * @param type The {@link TokenType} you expect the next token to be.
     * @return {@code true} if the consumed token has the expected type.
     * @throws ParseException If no valid token could be read.
     */
    public abstract boolean match(TokenType type) throws ParseException;



    /**
     * Consumes the next token and checks whether it has the same type as the given
     * token.
     * 
     * @param token The token (in most cases this is the current lookahead token).
     * @return {@code true} if the consumed token has the expected type.
     * @throws ParseException If no valid token could be read.
     */
    public abstract boolean match(Token token) throws ParseException;



    /**
     * Returns the next token without consuming it. That means:
     * 
     * {@code Token la = lookAhead() => match(la.getType()) == true}. 
     * @return The next token which will be consumed calling 
     *      {@link #match(TokenType)} or {@link #consume()}. 
     * @throws ParseException If no valid token could be read.
     */
    public abstract Token lookAhead() throws ParseException;



    /**
     * Consumes the next token.
     * 
     * @return The consumed token.
     * @throws ParseException If no valid token could be read.
     */
    public abstract Token consume() throws ParseException;

}