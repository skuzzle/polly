package de.skuzzle.polly.parsing;

import java.io.UnsupportedEncodingException;

import de.skuzzle.polly.parsing.tree.Root;


public abstract class AbstractParser<T extends AbstractTokenStream> {

    
    protected T scanner;
    
    
    
    public abstract Root parse(String input, String encoding) 
            throws ParseException, UnsupportedEncodingException;
    
    
    
    public Root parse(String input) throws ParseException {
        try {
            return this.parse(input, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    public Root tryParse(String input) {
        try {
            return this.tryParse(input, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    public abstract Root tryParse(String input, String encoding) 
            throws ParseException, UnsupportedEncodingException;
    
    
    
    protected Root parse(T scanner) 
            throws ParseException {
        this.scanner = scanner;
        return this.parse_input();
    }
    
    
    
    protected Root tryParse(T scanner) {
        try {
            return this.parse(scanner);
        } catch (ParseException e) {
            return null;
        }
    }

    
    
    protected void unexpectedToken(TokenType expected, Token found) 
            throws ParseException {
        throw new ParseException("Unerwartetes Symbol: '" + found.toString(false, false) + 
                "'. Erwartet: '" + expected.toString() + "'", 
                this.scanner.spanFrom(found));
    }
    
    
    
    protected Token expect(TokenType expected) throws ParseException {
        Token la = this.scanner.lookAhead();
        if (la.getType() != expected) {
            this.scanner.consume();
            this.unexpectedToken(expected, la);
        }
        this.scanner.consume();
        return la;
    }
    
    
    
    protected abstract Root parse_input() throws ParseException;
}
