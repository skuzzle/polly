package de.skuzzle.polly.core.parser.ast.visitor;

import de.skuzzle.polly.core.parser.ast.Identifier;
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
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;

/**
 * The Visitor interface is used to traverse the AST in a customizable way.
 *  
 * @author Simon Taddiken
 */
public interface ASTVisitor extends ASTTraversal {

    public boolean visit(Root root) throws ASTTraversalException;
    
    public boolean visit(Literal literal) throws ASTTraversalException;
    
    public boolean visit(Identifier identifier) throws ASTTraversalException;
    
    public boolean visit(ResolvableIdentifier id) throws ASTTraversalException;
    
    public boolean visit(Assignment assign) throws ASTTraversalException;
    
    public boolean visit(Declaration decl) throws ASTTraversalException;
    
    public boolean visit(Call call) throws ASTTraversalException;
    
    public boolean visit(OperatorCall call) throws ASTTraversalException;
    
    public boolean visit(Native nat) throws ASTTraversalException;
    
    public boolean visit(NamespaceAccess access) throws ASTTraversalException;
    
    public boolean visit(VarAccess access) throws ASTTraversalException;
    
    public boolean visit(FunctionLiteral func) throws ASTTraversalException;
    
    public boolean visit(ListLiteral list) throws ASTTraversalException;

    public boolean visit(ProductLiteral product) throws ASTTraversalException;

    public boolean visit(Braced braced) throws ASTTraversalException;
    
    public boolean visit(Delete delete) throws ASTTraversalException;

    public boolean visit(Inspect inspect) throws ASTTraversalException;
    
    public boolean visit(Problem problem) throws ASTTraversalException;
}