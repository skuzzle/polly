package de.skuzzle.polly.core.parser.ast;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;
import de.skuzzle.polly.tools.Equatable;



/**
 * Represents an Identifier Node in the AST.
 * 
 * @author Simon Taddiken
 */
public class Identifier extends Node implements Equatable {
    
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
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Identifier transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformIdentifier(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        return visitor.before(this) == ASTTraversal.CONTINUE && 
            visitor.after(this) == ASTTraversal.CONTINUE;
    }
    
    
    
    @Override
    public String toString() {
        return this.wasEscaped ? "\\" + this.getId() : this.getId();
    }



    @Override
    public boolean actualEquals(Equatable o) {
        final Identifier other = (Identifier) o;
        return this.id.equals(other.id);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Identifier.class;
    }
}
