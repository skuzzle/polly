package de.skuzzle.polly.core.parser.ast.expressions;

import java.util.concurrent.atomic.AtomicInteger;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;
import de.skuzzle.polly.core.parser.ast.visitor.resolving.AbstractTypeResolver;
import de.skuzzle.polly.core.parser.ast.visitor.resolving.TypeResolver;
import de.skuzzle.polly.tools.collections.Stack;


/**
 * This expression must be used for pre-provided functionality, for example operators. It
 * grants full compatibility of native implementations of operators and functions
 * and user defined functions. 
 * 
 * @author Simon Taddiken
 */
public abstract class Native extends Expression {
    
    private final static AtomicInteger PARAM_ID = new AtomicInteger();
    
    
    /**
     * Creates a unique parameter name.
     * 
     * @return A unique name.
     */
    protected final static String getParamName() {
        return "$param_" + PARAM_ID.getAndIncrement();
    }
    
    

    /**
     * Creates new HardcodedExpression with the given type.
     * 
     * @param type Type of this expression.
     */
    public Native(Type type) {
        super(Position.NONE, type);
    }
    
    
    
    /**
     * Executes this native expression. Execution of this expression must leave exactly
     * one new literal of the same type as this expression on the stack.
     * 
     * @param stack The stack used for execution. 
     * @param ns The current execution namespace.
     * @param execVisitor The visitor that is responsible for the execution of the AST.
     * @throws ASTTraversalException If executing fails.
     */
    public abstract void execute(Stack<Literal> stack, Namespace ns, 
            ASTVisitor execVisitor) throws ASTTraversalException;
    
    
    
    /**
     * Called by the {@link TypeResolver} when it hits an instance of this class during
     * traversal of the AST. Can be used to perform some custom context checking.
     * 
     * @param ns Current {@link Namespace} during type resolval.
     * @param typeResolver The type resolver.
     * @throws ASTTraversalException Can be thrown if context checking fails.
     */
    public abstract void resolveType(Namespace ns, AbstractTypeResolver typeResolver) 
        throws ASTTraversalException;
    
    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformNative(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        return visitor.before(this) == ASTTraversal.CONTINUE &&
            visitor.after(this) == ASTTraversal.CONTINUE;
    }
}
