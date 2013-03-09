package de.skuzzle.polly.core.parser.ast.visitor;

import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Braced;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.Problem;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;

/**
 * Defines methods for depth first traversal of an AST using 
 * {@link Node#traverse(ASTTraversal)}.
 * 
 * @author Simon Taddiken
 */
public interface ASTTraversal {
    
    /** 
     * Return value for <code>before</code> and <code>after</code> methods. Traversal 
     * will be stopped.
     */
    public final static int ABORT = -1;

    
    /** 
     * Return value for <code>before</code> and <code>after</code> methods. Traversal 
     * will continue.
     */
    public final static int CONTINUE = 0;
    
    /** 
     * Return value for <code>before</code>methods. Traversal will skip the current
     * sub tree. If returned by any of the <code>after</code> methods, traversal will
     * be stopped as if returning {@link #ABORT}.
     */
    public final static int SKIP = 1;
    
    
    
    public abstract int before(Root node) throws ASTTraversalException;
    public abstract int after(Root node) throws ASTTraversalException;



    public abstract int before(Literal node) throws ASTTraversalException;
    public abstract int after(Literal node) throws ASTTraversalException;



    public abstract int before(Identifier node) throws ASTTraversalException;
    public abstract int after(Identifier node) throws ASTTraversalException;
    
    
    
    public abstract int before(ResolvableIdentifier node) throws ASTTraversalException;
    public abstract int after(ResolvableIdentifier node) throws ASTTraversalException;



    public abstract int before(Assignment node) throws ASTTraversalException;
    public abstract int after(Assignment node) throws ASTTraversalException;



    public abstract int before(Declaration node) throws ASTTraversalException;
    public abstract int after(Declaration node) throws ASTTraversalException;



    public abstract int before(Call node) throws ASTTraversalException;
    public abstract int after(Call node) throws ASTTraversalException;



    public abstract int before(OperatorCall node) throws ASTTraversalException;
    public abstract int after(OperatorCall node) throws ASTTraversalException;



    public abstract int before(Native node) throws ASTTraversalException;
    public abstract int after(Native node) throws ASTTraversalException;



    public abstract int before(NamespaceAccess node) throws ASTTraversalException;
    public abstract int after(NamespaceAccess node) throws ASTTraversalException;



    public abstract int before(VarAccess node) throws ASTTraversalException;
    public abstract int after(VarAccess node) throws ASTTraversalException;



    public abstract int before(FunctionLiteral node) throws ASTTraversalException;
    public abstract int after(FunctionLiteral node) throws ASTTraversalException;



    public abstract int before(ListLiteral node) throws ASTTraversalException;
    public abstract int after(ListLiteral node) throws ASTTraversalException;



    public abstract int before(ProductLiteral node) throws ASTTraversalException;
    public abstract int after(ProductLiteral node) throws ASTTraversalException;



    public abstract int before(Braced node) throws ASTTraversalException;
    public abstract int after(Braced node) throws ASTTraversalException;



    public abstract int before(Delete node) throws ASTTraversalException;
    public abstract int after(Delete node) throws ASTTraversalException;



    public abstract int before(Inspect node) throws ASTTraversalException;
    public abstract int after(Inspect node) throws ASTTraversalException;

    
    public abstract int before(Problem node) throws ASTTraversalException;
    public abstract int after(Problem node) throws ASTTraversalException;
}