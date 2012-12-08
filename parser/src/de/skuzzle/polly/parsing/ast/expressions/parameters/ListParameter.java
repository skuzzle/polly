package de.skuzzle.polly.parsing.ast.expressions.parameters;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;



/**
 * This class represents a list parameter in a function declaration and takes a list
 * as actual value.
 * 
 * @author Simon Taddiken
 */
public class ListParameter extends Parameter {
    
    private static final long serialVersionUID = 1L;
    
    private final ResolvableIdentifier mainTypeName;
    
    
    
    public ListParameter(Position position, ResolvableIdentifier mainType,
            ResolvableIdentifier subType, ResolvableIdentifier name) {
        super(position, subType, name);
        this.mainTypeName = mainType;
    }
    
    
    
    /**
     * Create a new ListParameter which' subtype is already known.
     * 
     * @param position
     * @param name
     * @param subType
     */
    public ListParameter(Position position, ResolvableIdentifier name, Type subType) {
        super(position, name, subType);
        this.mainTypeName = new ResolvableIdentifier(Type.LIST.getTypeName());
    }
    
    
    
    /**
     * Gets the main type name (the type name before &lt;xyz&gt; subtype declaration)
     * 
     * @return The main types name
     */
    public ResolvableIdentifier getMainTypeName() {
        return this.mainTypeName;
    }
    
    
    
    /**
     * Returns the sub type name of the declared list.
     * 
     * @return The sub type.
     */
    @Override
    public ResolvableIdentifier getTypeName() {
        return super.getTypeName();
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitListParameter(this);
    }
}
