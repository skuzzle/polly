package de.skuzzle.polly.parsing.ast.declarations;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.Visitor;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;

/**
 * <p>This represents the declaration of a function. The type of this declaration will 
 * always be a {@link FunctionType}. The type of the evaluated value of the declared 
 * function can be determined using {@link #getExpression()}.</p>
 * 
 * @author Simon Taddiken
 */
public class FunctionDeclaration extends VarDeclaration {

    private final ArrayList<Parameter> formalParameters;
    
    
    
    /**
     * Creates a new FunctionDeclaration.
     * 
     * @param position The position of the declaration within the input String.
     * @param name The name of the declared function.
     * @param expression The expression representing the body of the function.
     * @param formalParameters Formal parameters of the function.
     */
    public FunctionDeclaration(Position position, Identifier name, 
            Expression expression, final Collection<Parameter> formalParameters) {
        super(position, name, expression);
        this.setType(this.createType(expression.getType(), formalParameters));
        this.formalParameters = new ArrayList<Parameter>(formalParameters);
    }
    
    
    
    private final FunctionType createType(Type returnType, 
            Collection<Parameter> parameters) {
        final Collection<Type> types = new ArrayList<Type>(parameters.size());
        for (final Parameter formal : parameters) {
            types.add(formal.getType());
        }
        return new FunctionType(returnType, types);
    }

    
    
    /**
     * Gets the formal parameters that this function was declared with.
     * 
     * @return Collection of formal parameters.
     */
    public ArrayList<Parameter> getFormalParameters() {
        return this.formalParameters;
    }
    

    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitFuncDecl(this);
    }
}
