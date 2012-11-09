package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

/**
 * ResolvableIdentifers are identifiers that refer to declarations. The actual declaration
 * of this identifier must be resolved during type checking.
 *  
 * @author Simon Taddiken
 *
 */
public class ResolvableIdentifier extends Identifier {

    private Declaration decl;
    
    public ResolvableIdentifier(Position position, String id) {
        super(position, id);
    }
    
    
    
    public ResolvableIdentifier(Identifier id) {
        super(id.getPosition(), id.getId());
    }
    
    
    
    /**
     * Sets the declaration of this identifier.
     * 
     * @param decl The declaration.
     */
    public void setDeclaration(Declaration decl) {
        this.decl = decl;
    }
    
    
    
    
    /**
     * Gets the declaration of this identifier.
     * 
     * @return The declaration.
     */
    public Declaration getDeclaration() {
        return this.decl;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitResolvable(this);
    }
}
