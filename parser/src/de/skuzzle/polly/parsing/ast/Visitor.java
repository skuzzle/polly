package de.skuzzle.polly.parsing.ast;

import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.AssignmentExpression;
import de.skuzzle.polly.parsing.ast.expressions.BinaryExpression;
import de.skuzzle.polly.parsing.ast.expressions.BinaryOperator;
import de.skuzzle.polly.parsing.ast.expressions.HardcodedExpression;
import de.skuzzle.polly.parsing.ast.expressions.VarOrCall;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
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
    public void afterdentifier(Identifier identifier) throws ASTTraversalException;
    
    /**
     * This method initiates traversing an {@link Identifier} Node of the AST.
     * 
     * @param identifier The Node to visit.
     * @throws ASTTraversalException May be thrown to abort further traversal of the AST.
     */
    public void visitIdentifier(Identifier identifier) throws ASTTraversalException;
    
    public void beforeBinaryOp(BinaryOperator op) throws ASTTraversalException;
    public void afterBinaryOp(BinaryOperator op) throws ASTTraversalException;
    public void visitBinaryOp(BinaryOperator op) throws ASTTraversalException;
    
    public void beforeBinaryExp(BinaryExpression binary) throws ASTTraversalException;
    public void afterBinaryExp(BinaryExpression binary) throws ASTTraversalException;
    public void visitBinaryExp(BinaryExpression binary) throws ASTTraversalException;
    
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
    
    
    public void beforeVarOrCall(VarOrCall call) throws ASTTraversalException;
    public void afterVarOrCall(VarOrCall call) throws ASTTraversalException;
    public void visitVarOrCall(VarOrCall call) throws ASTTraversalException;
    
    public void beforeHardCoded(HardcodedExpression hc) throws ASTTraversalException;
    public void afterHardCoded(HardcodedExpression hc) throws ASTTraversalException;
    public void visitHardCoded(HardcodedExpression hc) throws ASTTraversalException;
}