package de.skuzzle.polly.parsing.ast;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

/**
 * Represents an Identifier Node in the AST.
 * 
 * @author Simon Taddiken
 */
public class Identifier extends Node {

    private static final long serialVersionUID = 1L;
    
    private final String id;
    private final boolean wasEscaped;
    
    
    /**
     * Creates a new Identifier with an unknown position.
     * 
     * @param id Identifier String.
     */
    public Identifier(String id) {
        this(Position.NONE, id);
    }
    

    
    /**
     * Creates a new Identifier.
     * 
     * @param position Position of the identifier within the input String.
     * @param id Identifier String.
     * @param wasEscaped Whether this was an escaped identifier.
     */
    public Identifier(Position position, String id, boolean wasEscaped) {
        super(position);
        this.id = id;
        this.wasEscaped = wasEscaped;
    }
    
    
    
    /**
     * Creates a new Identifier.
     * 
     * @param position Position of the identifier within the input String.
     * @param id Identifier String.
     */
    public Identifier(Position position, String id) {
        this(position, id, false);
    }

    
    
    /**
     * Gets whether the identifier was created from an escaped token.
     * 
     * @return Whether the identifier was created from an escaped token.
     */
    public boolean wasEscaped() {
        return this.wasEscaped;
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
