package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.Inspect;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;

/**
 * The Visitor interface is used to traverse the AST in a customizable way.
 *  
 * @author Simon Taddiken
 */
public interface ASTVisitor {

    /**
     * This method is called before traversing a {@link Root} Node of the AST.
     * 
     * @param root The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void before(Root root) throws ASTTraversalException;
    
    /**
     * This method is called after traversing a {@link Root} Node of the AST.
     * 
     * @param root The visited Node.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void after(Root root) throws ASTTraversalException;
    
    /**
     * This method initiates traversing of a {@link Root} Node of the AST.
     * 
     * @param root The root Node to visit.
     * @throws ASTTraversalException
     */
    public void visit(Root root) throws ASTTraversalException;
    
    /**
     * This method is called before traversing a {@link Literal} Node of the AST.
     * 
     * @param literal The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void before(Literal literal) throws ASTTraversalException;
    
    /**
     * This method is called after visiting a {@link Literal} Node of the AST.
     * 
     * @param literal The visited Node.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void after(Literal literal) throws ASTTraversalException;
    
    /**
     * This method initiates traversing a {@link Literal} Node of the AST,
     * 
     * @param literal The {@link Literal} Node to visist
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void visit(Literal literal) throws ASTTraversalException;
    
    
    /**
     * This method is called before traversing an {@link Identifier} Node of the AST.
     * 
     * @param identifier The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void before(Identifier identifier) throws ASTTraversalException;
    
    /**
     * This method is called after traversing an {@link Identifier} Node of the AST.
     * 
     * @param identifier The visited Node.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void after(Identifier identifier) throws ASTTraversalException;
    
    /**
     * This method initiates traversing an {@link Identifier} Node of the AST.
     * 
     * @param identifier The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void visit(Identifier identifier) throws ASTTraversalException;
    
    public void before(ResolvableIdentifier id) throws ASTTraversalException;
    public void after(ResolvableIdentifier id) throws ASTTraversalException;
    public void visit(ResolvableIdentifier id) throws ASTTraversalException;
    
    public void before(Assignment assign) throws ASTTraversalException;
    public void after(Assignment assign) throws ASTTraversalException;
    public void visit(Assignment assign) throws ASTTraversalException;
    
    public void before(Declaration decl) throws ASTTraversalException;
    public void after(Declaration decl) throws ASTTraversalException;
    public void visit(Declaration decl) throws ASTTraversalException;
    
    public void before(Call call) throws ASTTraversalException;
    public void after(Call call) throws ASTTraversalException;
    public void visit(Call call) throws ASTTraversalException;
    
    public void before(OperatorCall call) throws ASTTraversalException;
    public void after(OperatorCall call) throws ASTTraversalException;
    public void visit(OperatorCall call) throws ASTTraversalException;
    
    public void before(Native nat) throws ASTTraversalException;
    public void after(Native nat) throws ASTTraversalException;
    public void visit(Native nat) throws ASTTraversalException;
    
    public void before(NamespaceAccess access) throws ASTTraversalException;
    public void after(NamespaceAccess access) throws ASTTraversalException;
    public void visit(NamespaceAccess access) throws ASTTraversalException;
    
    public void before(VarAccess access) throws ASTTraversalException;
    public void after(VarAccess access) throws ASTTraversalException;
    public void visit(VarAccess access) throws ASTTraversalException;
    
    public void before(FunctionLiteral func) throws ASTTraversalException;
    public void after(FunctionLiteral func) throws ASTTraversalException;
    public void visit(FunctionLiteral func) throws ASTTraversalException;
    
    public void before(ListLiteral list) throws ASTTraversalException;
    public void after(ListLiteral list) throws ASTTraversalException;
    public void visit(ListLiteral list) throws ASTTraversalException;

    public void before(ProductLiteral product) throws ASTTraversalException;
    public void after(ProductLiteral product) throws ASTTraversalException;
    public void visit(ProductLiteral product) throws ASTTraversalException;

    
    public void before(Braced braced) throws ASTTraversalException;
    public void after(Braced braced) throws ASTTraversalException;
    public void visit(Braced braced) throws ASTTraversalException;
    
    public void before(Delete delete) throws ASTTraversalException;
    public void after(Delete delete) throws ASTTraversalException;
    public void visit(Delete delete) throws ASTTraversalException;

    public void before(Inspect inspect) throws ASTTraversalException;
    public void after(Inspect inspect) throws ASTTraversalException;
    public void visit(Inspect inspect) throws ASTTraversalException;
}