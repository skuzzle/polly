package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.Visitor;

/**
 * Represents an Identifier Node in the AST.
 * 
 * @author Simon Taddiken
 */
public class Identifier extends Expression {

    private final String id;
    
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



    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitIdentifier(this);
    }
}
