package de.skuzzle.polly.parsing.ast.expressions;

import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;


public class Delete extends Expression {

    private static final long serialVersionUID = 1L;
    
    public final static class DeleteableIdentifier extends Identifier {
        
        private static final long serialVersionUID = 1L;
        
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
    
    
    
    public Delete(Position position, List<DeleteableIdentifier> ids) {
        super(position);
        this.ids = ids;
    }

    
    
    public List<DeleteableIdentifier> getIdentifiers() {
        return this.ids;
    }
    
    

    @Override
    public void visit(ASTVisitor visitor) throws ASTTraversalException {
        visitor.visit(this);
    }
    
    
    
    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformDelete(this);
    }
}
