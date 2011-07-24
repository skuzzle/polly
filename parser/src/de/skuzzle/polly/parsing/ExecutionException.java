package de.skuzzle.polly.parsing;

public class ExecutionException extends ParseException {

    private static final long serialVersionUID = 1L;

    
    
    public ExecutionException(String error, Position position) {
        super(error, position);
    }

    
    
    @Override
    public String getMessage() {
        return "Laufzeitfehler: " + this.errorMessage + 
            " (in Eingabe an Position " + this.position.toString() + ")";
    }
}
