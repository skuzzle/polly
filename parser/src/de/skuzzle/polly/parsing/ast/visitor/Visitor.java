package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.AssignmentExpression;
import de.skuzzle.polly.parsing.ast.expressions.HardcodedExpression;
import de.skuzzle.polly.parsing.ast.expressions.LambdaCall;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
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
    
    public void beforeAssignment(AssignmentExpression assign) throws ASTTraversalException;
    public void afterAssignment(AssignmentExpression assign) throws ASTTraversalException;
    public void visitAssignment(AssignmentExpression assign) throws ASTTraversalException;
    
    public void beforeParameter(Parameter param) throws ASTTraversalException;
    public void afterParameter(Parameter param) throws ASTTraversalException;
    public void visitParameter(Parameter param) throws ASTTraversalException;
    
    public void beforeVarDecl(VarDeclaration decl) throws ASTTraversalException;
    public void afterVarDecl(VarDeclaration decl) throws ASTTraversalException;
    public void visitVarDecl(VarDeclaration decl) throws ASTTraversalException;
    
    public void beforeFuncDecl(FunctionDeclaration decl) throws ASTTraversalException;
    public void afterFuncDecl(FunctionDeclaration decl) throws ASTTraversalException;
    public void visitFuncDecl(FunctionDeclaration decl) throws ASTTraversalException;
    
    public void beforeCall(Call call) throws ASTTraversalException;
    public void afterCall(Call call) throws ASTTraversalException;
    public void visitCall(Call call) throws ASTTraversalException;
    
    public void beforeLambdaCall(LambdaCall call) throws ASTTraversalException;
    public void afterLambdaCall(LambdaCall call) throws ASTTraversalException;
    public void visitLambdaCall(LambdaCall call) throws ASTTraversalException;
    
    public void beforeHardCoded(HardcodedExpression hc) throws ASTTraversalException;
    public void afterHardCoded(HardcodedExpression hc) throws ASTTraversalException;
    public void visitHardCoded(HardcodedExpression hc) throws ASTTraversalException;
    
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException;
    public void afterAccess(NamespaceAccess access) throws ASTTraversalException;
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException;
    
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException;
    public void afterVarAccess(VarAccess access) throws ASTTraversalException;
    public void visitVarAccess(VarAccess access) throws ASTTraversalException;
}