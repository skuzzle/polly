package de.skuzzle.polly.parsing.ast.expressions.parameters;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;



/**
 * This class represents a list parameter in a function declaration and takes a list
 * as actual value.
 * 
 * @author Simon Taddiken
 */
public class ListParameter extends Parameter {
    
    private static final long serialVersionUID = 1L;
    
    
    
    public ListParameter(Position position, ResolvableIdentifier subType, 
            ResolvableIdentifier name) {
        super(position, subType, name);
    }
    
    
    
    /**
     * Create a new ListParameter which' subtype is already known.
     * 
     * @param position Position of this Parameter.
     * @param name name of the parameter.
     * @param subType Subtype of this parameter.
     */
    public ListParameter(Position position, ResolvableIdentifier name, Type subType) {
        super(position, name, new ListTypeConstructor(subType));
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
