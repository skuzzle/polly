package de.skuzzle.polly.parsing;

public class ParseException extends Exception {

    private static final long serialVersionUID = 1L;

    protected String errorMessage;
    protected Position position;
    

    
    public ParseException(String error, Position position) {
        this.errorMessage = error;
        this.position = position;
    }
    
    
    
    public Position getPosition() {
        return this.position;
    }
    
    
    
    @Override
    public String getMessage() {
        return "Syntax Fehler: " + this.errorMessage + 
            " (in Eingabe an Position " + this.position.toString() + ")";
    }
}
