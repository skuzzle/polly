package de.skuzzle.polly.parsing.ast.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.ResolvedParameter;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;


public class Call extends VarAccess {
    
    private final List<Expression> parameters;
    private List<ResolvedParameter> resolvedParameters;
    
    

    /**
     * Creates a new function call.
     * 
     * @param position Position of the call within the source.
     * @param identifier Name of the function being called
     * @param parameters Actual parameters of the call.
     */
    public Call(Position position, ResolvableIdentifier identifier, 
            Collection<Expression> parameters) {
        super(position, identifier);
        this.parameters = new ArrayList<Expression>(parameters);
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
    
    
    
    
    /**
     * Sets the resolved parameters for this call.
     * 
     * @param resolvedParameters The resolved parameters.
     * @see #getResolvedParameters()
     */
    public void setResolvedParameters(List<ResolvedParameter> resolvedParameters) {
        this.resolvedParameters = resolvedParameters;
    }
    
    
    
    /**
     * Gets the list of resolved parameters. That is, a list that contains the formal 
     * parameter's name, type and the actual parameter's expression. This attribute is 
     * available after type checking is done.
     * 
     * @return List of resolved parameters.
     */
    public List<ResolvedParameter> getResolvedParameters() {
        return this.resolvedParameters;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitCall(this);
    }
}
