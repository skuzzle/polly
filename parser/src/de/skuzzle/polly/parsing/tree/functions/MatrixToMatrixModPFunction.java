package de.skuzzle.polly.parsing.tree.functions;

import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.functions.Functions.MatrixType;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;
import de.skuzzle.polly.parsing.types.Type;


public class MatrixToMatrixModPFunction extends FunctionDeclaration {

    private static final long serialVersionUID = 1L;

    public MatrixToMatrixModPFunction(String name, MatrixType matType) {
        this(name, new Functions.MatrixModPFunction(matType));
    }
    
    
    
    public MatrixToMatrixModPFunction(String name, Expression function) {
        super(new IdentifierLiteral(name), true);
        this.getFormalParameters().add(
            new VarDeclaration(
                new IdentifierLiteral("matrix"), 
                de.skuzzle.polly.parsing.types.MatrixType.MATRIX));
        
        this.getFormalParameters().add(
            new VarDeclaration(
                new IdentifierLiteral("p"), Type.NUMBER));
        
        this.setExpression(function);
    }

}
