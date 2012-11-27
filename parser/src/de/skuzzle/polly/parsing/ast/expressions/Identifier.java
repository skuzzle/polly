package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

/**
 * Represents an Identifier Node in the AST.
 * 
 * @author Simon Taddiken
 */
public class Identifier extends Expression {

    private static final long serialVersionUID = 1L;
    
    private final String id;
    
    
    /**
     * Creates a new Identifier with an unknown position.
     * 
     * @param id Identifier String.
     */
    public Identifier(String id) {
        this(Position.EMPTY, id);
    }
    
    
    
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
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof Identifier)) {
            return false;
        }
        final Identifier other = (Identifier)obj;
        return this.id == null && other.id == null || this.id.equals(other.id);
    }
    
    
    
    @Override
    public String toString() {
        return this.getId();
    }
}
