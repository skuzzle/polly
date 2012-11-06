package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.Visitor;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;

/**
 * Represents an Identifier Node in the AST.
 * 
 * @author Simon Taddiken
 */
public class Identifier extends Expression {

    private final String id;
    private Declaration decl;
    
    
    
    /**
     * Creates a new Identifier.
     * 
     * @param position Position of the identifier within the input String.
     * @param id Identifier String.
     */
    public Identifier(Position position, String id) {
        super(position);
        this.id = id;
    }

    
    
    /**
     * Gets the identifier String.
     * 
     * @return The identifier String.
     */
    public String getId() {
        return this.id;
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
        visitor.visitIdentifier(this);
    }
}
