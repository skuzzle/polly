package de.skuzzle.polly.parsing;

import de.skuzzle.polly.parsing.tree.TreeElement;


public abstract class AbstractParser<T extends AbstractTokenStream> {

    
    protected T scanner;
    
    
    
    protected synchronized TreeElement parse(T scanner) 
            throws ParseException {
        this.scanner = scanner;
        return this.parse_input();
    }
    
    
    
    protected synchronized TreeElement tryParse(T scanner) {
        try {
            return this.parse(scanner);
        } catch (ParseException e) {
            return null;
        }
    }

    
    
    protected void unexpectedToken(TokenType expected, Token found) 
            throws ParseException {
        throw new ParseException("Unerwartetes Symbol: " + found.toString(false, false) + 
                ". Erwartet: " + expected.toString(), this.scanner.spanFrom(found));
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
    
    
    
    protected abstract TreeElement parse_input() throws ParseException;
}
