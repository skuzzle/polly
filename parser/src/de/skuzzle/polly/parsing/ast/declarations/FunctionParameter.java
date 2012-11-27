package de.skuzzle.polly.parsing.ast.declarations;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;

/**
 * Represents a formal parameter which itself takes a function as actual value.
 * 
 * @author Simon Taddiken
 */
public class FunctionParameter extends Parameter {

    private static final long serialVersionUID = 1L;
    
    private final Collection<ResolvableIdentifier> signature;
    
    /**
     * Creates a new Function parameter.
     * 
     * @param position Position within the input string.
     * @param sig The functions signature. The first entry will be interpreted as the
     *          return type. Collection must thus have at least one entry in order not to
     *          produce errors.
     * @param name
     */
    public FunctionParameter(Position position, Collection<ResolvableIdentifier> sig,
            ResolvableIdentifier name) {
        super(position, null, name);
        this.signature = sig;
    }
    
    
    
    /**
     * Creates a function parameter which' type is already known.
     * @param position
     * @param returnType
     * @param sig
     * @param name
     */
    public FunctionParameter(Position position, Type returnType, Collection<Type> sig, 
            ResolvableIdentifier name) {
        super(position, name, new FunctionType(returnType, sig));
        this.signature = new ArrayList<ResolvableIdentifier>(sig.size() + 1);
        this.signature.add(new ResolvableIdentifier(returnType.getTypeName()));
        for (final Type t : sig) {
            this.signature.add(new ResolvableIdentifier(t.getTypeName()));
        }
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
