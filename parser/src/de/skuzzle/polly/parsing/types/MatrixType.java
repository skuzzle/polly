package de.skuzzle.polly.parsing.types;


public class MatrixType extends ListType {

    private static final long serialVersionUID = 1L;
    
    public final static Type MATRIX = new MatrixType();
    
    
    protected MatrixType() {
        super(new ListType(Type.NUMBER));
    }


    
    @Override
    public String toString() {
        return "Matrix";
    }
}
