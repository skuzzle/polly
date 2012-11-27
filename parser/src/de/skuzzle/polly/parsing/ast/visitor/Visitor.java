package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.FunctionParameter;
import de.skuzzle.polly.parsing.ast.declarations.ListParameter;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Hardcoded;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;

/**
 * The Visitor interface is used to traverse the AST in a customizable way.
 *  
 * @author Simon Taddiken
 */
public interface Visitor {

    /**
     * This method is called before traversing a {@link Root} Node of the AST.
     * 
     * @param root The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void beforeRoot(Root root) throws ASTTraversalException;
    
    /**
     * This method is called after traversing a {@link Root} Node of the AST.
     * 
     * @param root The visited Node.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void afterRoot(Root root) throws ASTTraversalException;
    
    /**
     * This method initiates traversing of a {@link Root} Node of the AST.
     * 
     * @param root The root Node to visit.
     * @throws ASTTraversalException
     */
    public void visitRoot(Root root) throws ASTTraversalException;
    
    /**
     * This method is called before traversing a {@link Literal} Node of the AST.
     * 
     * @param literal The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void beforeLiteral(Literal literal) throws ASTTraversalException;
    
    /**
     * This method is called after visiting a {@link Literal} Node of the AST.
     * 
     * @param literal The visited Node.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void afterLiteral(Literal literal) throws ASTTraversalException;
    
    /**
     * This method initiates traversing a {@link Literal} Node of the AST,
     * 
     * @param literal The {@link Literal} Node to visist
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void visitLiteral(Literal literal) throws ASTTraversalException;
    
    
    /**
     * This method is called before traversing an {@link Identifier} Node of the AST.
     * 
     * @param identifier The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void beforeIdentifier(Identifier identifier) throws ASTTraversalException;
    
    /**
     * This method is called after traversing an {@link Identifier} Node of the AST.
     * 
     * @param identifier The visited Node.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void afterIdentifier(Identifier identifier) throws ASTTraversalException;
    
    /**
     * This method initiates traversing an {@link Identifier} Node of the AST.
     * 
     * @param identifier The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void visitIdentifier(Identifier identifier) throws ASTTraversalException;
    
    public void beforeResolvable(ResolvableIdentifier id) throws ASTTraversalException;
    public void afterResolvable(ResolvableIdentifier id) throws ASTTraversalException;
    public void visitResolvable(ResolvableIdentifier id) throws ASTTraversalException;
    
    public void beforeAssignment(Assignment assign) throws ASTTraversalException;
    public void afterAssignment(Assignment assign) throws ASTTraversalException;
    public void visitAssignment(Assignment assign) throws ASTTraversalException;
    
    public void beforeParameter(Parameter param) throws ASTTraversalException;
    public void afterParameter(Parameter param) throws ASTTraversalException;
    public void visitParameter(Parameter param) throws ASTTraversalException;
    
    public void beforeListParameter(ListParameter param) throws ASTTraversalException;
    public void afterListParameter(ListParameter param) throws ASTTraversalException;
    public void visitListParameter(ListParameter param) throws ASTTraversalException;
    
    
    public void beforeFunctionParameter(FunctionParameter param) 
            throws ASTTraversalException;
    public void afterFunctionParameter(FunctionParameter param) 
            throws ASTTraversalException;
    public void visitFunctionParameter(FunctionParameter param) 
            throws ASTTraversalException;
    
    public void beforeVarDecl(VarDeclaration decl) throws ASTTraversalException;
    public void afterVarDecl(VarDeclaration decl) throws ASTTraversalException;
    public void visitVarDecl(VarDeclaration decl) throws ASTTraversalException;
    
    public void beforeCall(Call call) throws ASTTraversalException;
    public void afterCall(Call call) throws ASTTraversalException;
    public void visitCall(Call call) throws ASTTraversalException;
    
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException;
    public void afterOperatorCall(OperatorCall call) throws ASTTraversalException;
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException;
    
    public void beforeHardcoded(Hardcoded hc) throws ASTTraversalException;
    public void afterHardcoded(Hardcoded hc) throws ASTTraversalException;
    public void visitHardcoded(Hardcoded hc) throws ASTTraversalException;
    
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException;
    public void afterAccess(NamespaceAccess access) throws ASTTraversalException;
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException;
    
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException;
    public void afterVarAccess(VarAccess access) throws ASTTraversalException;
    public void visitVarAccess(VarAccess access) throws ASTTraversalException;
    
    public void beforeFunctionLiteral(FunctionLiteral func) throws ASTTraversalException;
    public void afterFunctionLiteral(FunctionLiteral func) throws ASTTraversalException;
    public void visitFunctionLiteral(FunctionLiteral func) throws ASTTraversalException;
    
    public void beforeListLiteral(ListLiteral list) throws ASTTraversalException;
    public void afterListLiteral(ListLiteral list) throws ASTTraversalException;
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException;

    public void beforeBraced(Braced braced) throws ASTTraversalException;
    public void afterBraced(Braced braced) throws ASTTraversalException;
    public void visitBraced(Braced braced) throws ASTTraversalException;
}