package de.skuzzle.polly.parsing.ast.declarations;

import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class FunctionParameter extends Parameter {

    private final Collection<ResolvableIdentifier> signature;
    
    public FunctionParameter(Position position, Collection<ResolvableIdentifier> sig,
            ResolvableIdentifier name) {
        super(position, null, name);
        this.signature = sig;
    }

    
    
    /**
     * Gets the declared signature. The first {@link ResolvableIdentifier} in the 
     * returned collection is the return type's name.
     * 
     * @return Collection of type names representing a function's signature.
     */
    public Collection<ResolvableIdentifier> getSignature() {
        return this.signature;
    }
    
    
    
    /**
     * This method will always return <code>null</code> on instances of this class!
     * 
     * @return <code>null</code>!
     */
    @Override
    public ResolvableIdentifier getTypeName() {
        return super.getTypeName();
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitFunctionParameter(this);
    }
}
