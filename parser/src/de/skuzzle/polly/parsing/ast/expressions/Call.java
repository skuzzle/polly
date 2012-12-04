package de.skuzzle.polly.parsing.ast.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;


public class Call extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    private Expression lhs;
    private final List<Expression> parameters;
    private final Position parameterPosition;
    
    
    
    public Call(Position position, Expression lhs, Collection<Expression> parameters, 
        Position parameterPosition) {
        super(position);
        this.parameters = new ArrayList<Expression>(parameters);
        this.lhs = lhs;
        this.parameterPosition = parameterPosition;
    }
    
    
    
    
    public Position getParameterPosition() {
        return this.parameterPosition;
    }
    
    
    
    @Override
    public <T extends Node> void replaceChild(T current, T newChild) {
        if (current == this.lhs) {
            this.lhs = (Expression) newChild;
        } else {
            for (int i = 0; i < this.parameters.size(); ++i) {
                if (this.parameters.get(i) == current) {
                    this.parameters.set(i, (Expression) newChild);
                    return;
                }
            }
            super.replaceChild(current, newChild);
        }
    }
    
    
    
    public Expression getLhs() {
        return this.lhs;
    }
    
    
    
    /**
     * Creates a {@link FunctionType} signature for this call which can be used to 
     * resolve the matching declaration from a {@link Namespace}. The returned type's
     * <code>returnType</code> will be {@link Type#ANY}, causing var resolution to 
     * disregard the return type.
     * 
     * @return A new {@link FunctionType} suitable to resolve the declaration for the
     *          called function.
     */
    public FunctionType createSignature() {
        final Collection<Type> types = new ArrayList<Type>(this.parameters.size());
        for (final Expression exp : this.parameters) {
            types.add(exp.getType());
        }
        // We do not know the return type yet, so chose one that will match every
        // type
        return new FunctionType(Type.ANY, types);
    }
    
    
    
    /**
     * Gets the list of actual parameters of this call.
     * 
     * @return The parameter list.
     */
    public List<Expression> getParameters() {
        return this.parameters;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitCall(this);
    }
    
    
    
    @Override
    public String toString() {
        return "[Call: " + this.parameters.size() + " params, type: " + 
            this.getType() + "]";
    }
}
