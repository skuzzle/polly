package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.parser.dom.ASTNode;
import de.skuzzle.parser.dom.AbstractASTNode;
import de.skuzzle.polly.dom.ASTPollyNode;
import de.skuzzle.polly.dom.ASTVisitor;



public abstract class AbstractPollyNode extends AbstractASTNode<ASTVisitor> 
        implements ASTPollyNode {

    /** Whether to continue traversal */
    protected final static int PROCESS_CONTINUE = ASTVisitor.PROCESS_CONTINUE;
    
    /** Aborts traversal */
    protected final static int PROCESS_ABORT = ASTVisitor.PROCESS_ABORT;
    
    /** Skips the current sub tree */
    protected final static int PROCESS_SKIP = ASTVisitor.PROCESS_SKIP;
    
 
    /** Error message for when trying to add a node as its own child */
    protected final static String ERROR_SELF_AS_CHILD = "can not add self as child"; //$NON-NLS-1$
    
    /** Error message for when trying to add a node's parent as its child */
    protected final static String ERROR_PARENT_AS_CHILD = "can not add parent node as child"; //$NON-NLS-1$
    
    /** Error message for when trying to replace a node which is not part of the current node */
    protected final static String ERROR_NOT_A_CHILD = "provided node is not a child node"; //$NON-NLS-1$
    
    /** Error message for when trying to replace a child with an invalid node */
    protected final static String ERROR_NOT_EXPECTED_TYPE = "provided node has not the expected type"; //$NON-NLS-1$
    
    
    /** NodeFactory instance shared among all nodes */
    private final static DefaultASTNodeFactory NODE_FACTORY = new DefaultASTNodeFactory();
    
    
    
    @Override
    public DefaultASTNodeFactory getNodeFactory() {
        return NODE_FACTORY;
    }
    
    
    
    @Override
    public void setOrigin(ASTNode<ASTVisitor> origin) {
        super.setOrigin(origin);
    }
    
    
    
    @Override
    public ASTPollyNode getOrigin() {
        return super.getOrigin().as(ASTPollyNode.class);
    }
    
    
    
    @Override
    public ASTPollyNode deepOrigin() {
        return super.deepOrigin().as(ASTPollyNode.class);
    }
}
