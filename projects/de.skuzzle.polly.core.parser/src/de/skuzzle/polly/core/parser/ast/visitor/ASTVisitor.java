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
import de.skuzzle.polly.core.parser.ast.expressions.Problem;
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

    public boolean visit(Root node) throws ASTTraversalException;
    
    public boolean visit(Literal node) throws ASTTraversalException;
    
    public boolean visit(Identifier node) throws ASTTraversalException;
    
    public boolean visit(ResolvableIdentifier node) throws ASTTraversalException;
    
    public boolean visit(Assignment node) throws ASTTraversalException;
    
    public boolean visit(Declaration node) throws ASTTraversalException;
    
    public boolean visit(Call node) throws ASTTraversalException;
    
    public boolean visit(OperatorCall node) throws ASTTraversalException;
    
    public boolean visit(Native node) throws ASTTraversalException;
    
    public boolean visit(NamespaceAccess node) throws ASTTraversalException;
    
    public boolean visit(VarAccess node) throws ASTTraversalException;
    
    public boolean visit(FunctionLiteral node) throws ASTTraversalException;
    
    public boolean visit(ListLiteral node) throws ASTTraversalException;

    public boolean visit(ProductLiteral node) throws ASTTraversalException;

    public boolean visit(Braced node) throws ASTTraversalException;
    
    public boolean visit(Delete node) throws ASTTraversalException;

    public boolean visit(Inspect node) throws ASTTraversalException;

    public boolean visit(Problem node) throws ASTTraversalException;
}