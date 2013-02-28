package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.Inspect;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;

/**
 * Defines methods for depth first traversal of an AST.
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
    
    
    
    public abstract int before(Root root) throws ASTTraversalException;
    public abstract int after(Root root) throws ASTTraversalException;



    public abstract int before(Literal literal) throws ASTTraversalException;
    public abstract int after(Literal literal) throws ASTTraversalException;



    public abstract int before(Identifier identifier) throws ASTTraversalException;
    public abstract int after(Identifier identifier) throws ASTTraversalException;
    
    
    
    public abstract int before(ResolvableIdentifier id) throws ASTTraversalException;
    public abstract int after(ResolvableIdentifier id) throws ASTTraversalException;



    public abstract int before(Assignment assign) throws ASTTraversalException;
    public abstract int after(Assignment assign) throws ASTTraversalException;



    public abstract int before(Declaration decl) throws ASTTraversalException;
    public abstract int after(Declaration decl) throws ASTTraversalException;



    public abstract int before(Call call) throws ASTTraversalException;
    public abstract int after(Call call) throws ASTTraversalException;



    public abstract int before(OperatorCall call) throws ASTTraversalException;
    public abstract int after(OperatorCall call) throws ASTTraversalException;



    public abstract int before(Native nat) throws ASTTraversalException;
    public abstract int after(Native nat) throws ASTTraversalException;



    public abstract int before(NamespaceAccess access) throws ASTTraversalException;
    public abstract int after(NamespaceAccess access) throws ASTTraversalException;



    public abstract int before(VarAccess access) throws ASTTraversalException;
    public abstract int after(VarAccess access) throws ASTTraversalException;



    public abstract int before(FunctionLiteral func) throws ASTTraversalException;
    public abstract int after(FunctionLiteral func) throws ASTTraversalException;



    public abstract int before(ListLiteral list) throws ASTTraversalException;
    public abstract int after(ListLiteral list) throws ASTTraversalException;



    public abstract int before(ProductLiteral product) throws ASTTraversalException;
    public abstract int after(ProductLiteral product) throws ASTTraversalException;



    public abstract int before(Braced braced) throws ASTTraversalException;
    public abstract int after(Braced braced) throws ASTTraversalException;



    public abstract int before(Delete delete) throws ASTTraversalException;
    public abstract int after(Delete delete) throws ASTTraversalException;



    public abstract int before(Inspect inspect) throws ASTTraversalException;
    public abstract int after(Inspect inspect) throws ASTTraversalException;

}