package de.skuzzle.polly.parsing.ast.expressions;

import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;


/**
 * <p>Represents a delete statements. Those hold a list of 
 * {@link DeleteableIdentifier DeleteableIdentifiers} that will be deleted upon 
 * execution.</p>
 * 
 * <p>As we there are no real statements, this node will return the number of deleted 
 * identifiers upon execution.</p>
 * 
 * @author Simon Taddiken
 */
public class Delete extends Expression {
   
    /**
     * Identifier extension which contains a declaration scope to determine from which
     * {@link Namespace} a declaration of an identifier will be deleted. 
     * 
     * @author Simon Taddiken
     */
    public final static class DeleteableIdentifier extends Identifier {
        
        private final boolean global;
        
        public DeleteableIdentifier(Identifier id, boolean global) {
            super(id.getPosition(), id.getId());
            this.global = global;
        }
        
        
        
        /**
         * Whether identifier should be delete from PUBLIC namespace.
         * 
         * @return Whether identifier should be delete from PUBLIC namespace.
         */
        public boolean isGlobal() {
            return this.global;
        }
    }
    
    private final List<DeleteableIdentifier> ids;
    
    
    
    /**
     * Creates a new delete statement.
     * @param position
     * @param ids
     */
    public Delete(Position position, List<DeleteableIdentifier> ids) {
        super(position);
        this.ids = ids;
    }

    
    
    /**
     * Gets a list of names that should be deleted from current or from public 
     * {@link Namespace}.
     * 
     * @return A list of names.
     */
    public List<DeleteableIdentifier> getIdentifiers() {
        return this.ids;
    }
    
    

    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        for (final DeleteableIdentifier id : this.ids) {
            if (!id.traverse(visitor)) {
                return false;
            }
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
    
    
    
    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformDelete(this);
    }
}
