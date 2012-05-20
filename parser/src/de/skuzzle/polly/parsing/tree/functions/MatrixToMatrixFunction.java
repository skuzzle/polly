package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.functions.Functions.MatrixType;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;


public class MatrixToMatrixFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;

    public MatrixToMatrixFunction(String name, MatrixType matType) {
        this(name, new Functions.MatrixFunction(matType));
    }
    
    
    
    public MatrixToMatrixFunction(String name, Expression function) {
        super(new IdentifierLiteral(name), true);
        this.getFormalParameters().add(
            new VarDeclaration(
                new IdentifierLiteral("matrix"), 
                de.skuzzle.polly.parsing.types.MatrixType.MATRIX));
        
        this.setExpression(function);
    }

}
