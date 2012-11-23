package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

/**
 * This class represents a list parameter in a function declaration and takes a list
 * as actual value.
 * 
 * @author Simon Taddiken
 */
public class ListParameter extends Parameter {

    private final ResolvableIdentifier mainTypeName;
    
    public ListParameter(Position position, ResolvableIdentifier mainTypeName, 
            ResolvableIdentifier subType, ResolvableIdentifier name) {
        super(position, subType, name);
        this.mainTypeName = mainTypeName;
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
     * Returns the sub type of the declared list.
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
