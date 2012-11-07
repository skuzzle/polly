package de.skuzzle.polly.parsing.ast.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;


public class Call extends VarAccess {
    
    private final List<Expression> parameters;
    private boolean isOperator;
    
    
    
    /**
     * Constructor for sub classes.
     * 
     * @param position Position of the call within the source.
     * @param parameters Actual parameters of function call.
     */
    protected Call(Position position, Collection<Expression> parameters) {
        this(position, null, parameters);
    }
    
    

    public Call(Position position, Identifier identifier, 
            Collection<Expression> parameters) {
        super(position, identifier);
        this.parameters = new ArrayList<Expression>(parameters);
    }
    
    
    
    public FunctionType createSignature() {
        final Collection<Type> types = new ArrayList<Type>(this.parameters.size());
        for (final Expression exp : this.parameters) {
            types.add(exp.getType());
        }
        // We do not know the return type yet, so chose one that will match every
        // type
        return new FunctionType(Type.ANY, types);
    }
    
    
    
    
    public void setOperator(boolean isOperator) {
        this.isOperator = isOperator;
    }
    
    
    
    public boolean isOperator() {
        return this.isOperator;
    }
    
    
    
    public List<Expression> getParameters() {
        return this.parameters;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitCall(this);
    }
}
