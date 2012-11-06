package de.skuzzle.polly.parsing.ast;

import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.AssignmentExpression;
import de.skuzzle.polly.parsing.ast.expressions.BinaryExpression;
import de.skuzzle.polly.parsing.ast.expressions.BinaryOperator;
import de.skuzzle.polly.parsing.ast.expressions.HardcodedExpression;
import de.skuzzle.polly.parsing.ast.expressions.VarOrCall;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;

/**
 * {@link Visitor} implementation that traverses the AST in depth-first order. 
 * Implementation of <code>beforeXY</code> and <code>afterXY</code> methods is empty and
 * may be overridden by sub classes.
 * 
 * @author Simon Taddiken
 */
public class DepthFirstVisitor implements Visitor {

    @Override
    public void beforeRoot(Root root) throws ASTTraversalException {}

    @Override
    public void afterRoot(Root root) throws ASTTraversalException {}

    @Override
    public void visitRoot(Root root) throws ASTTraversalException {
        this.beforeRoot(root);
        for (final Expression exp : root.getExpressions()) {
            exp.visit(this);
        }
        this.afterRoot(root);
    }

    
    
    @Override
    public void beforeLiteral(Literal literal) throws ASTTraversalException {}

    @Override
    public void afterLiteral(Literal literal) throws ASTTraversalException {}

    @Override
    public void visitLiteral(Literal literal) throws ASTTraversalException {
        this.beforeLiteral(literal);
        this.afterLiteral(literal);
    }

    
    
    @Override
    public void beforeIdentifier(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void afterdentifier(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void visitIdentifier(Identifier identifier)  throws ASTTraversalException {
        this.beforeIdentifier(identifier);
        this.afterdentifier(identifier);
    }
    
    

    @Override
    public void beforeBinaryOp(BinaryOperator op) throws ASTTraversalException {}

    @Override
    public void afterBinaryOp(BinaryOperator op) throws ASTTraversalException {}

    @Override
    public void visitBinaryOp(BinaryOperator op) throws ASTTraversalException {
        this.beforeBinaryOp(op);
        this.afterBinaryOp(op);
    }

    
    
    @Override
    public void beforeBinaryExp(BinaryExpression binary) throws ASTTraversalException {}

    @Override
    public void afterBinaryExp(BinaryExpression binary) throws ASTTraversalException {}

    @Override
    public void visitBinaryExp(BinaryExpression binary) throws ASTTraversalException {
        this.beforeBinaryExp(binary);
        binary.getLeft().visit(this);
        binary.getOperator().visit(this);
        binary.getRight().visit(this);
        this.afterBinaryExp(binary);
    }

    
    
    @Override
    public void beforeAssignment(AssignmentExpression assign)
        throws ASTTraversalException {}

    @Override
    public void afterAssignment(AssignmentExpression assign)
        throws ASTTraversalException {}

    @Override
    public void visitAssignment(AssignmentExpression assign) 
            throws ASTTraversalException {
        this.beforeAssignment(assign);
        assign.getExpression().visit(this);
        assign.getDeclaration().visit(this);
        this.afterAssignment(assign);
    }

    
    
    @Override
    public void beforeParameter(Parameter param) throws ASTTraversalException {}

    @Override
    public void afterParameter(Parameter param) throws ASTTraversalException {}

    @Override
    public void visitParameter(Parameter param) throws ASTTraversalException {
        this.beforeParameter(param);
        this.afterParameter(param);
    }

    
    
    @Override
    public void beforeVarDecl(VarDeclaration decl) throws ASTTraversalException {}

    @Override
    public void afterVarDecl(VarDeclaration decl) throws ASTTraversalException {}

    @Override
    public void visitVarDecl(VarDeclaration decl) throws ASTTraversalException {
        this.beforeVarDecl(decl);
        decl.getExpression().visit(this);
        decl.getName().visit(this);
        this.afterVarDecl(decl);
    }

    
    
    @Override
    public void beforeFuncDecl(FunctionDeclaration decl) throws ASTTraversalException {}

    @Override
    public void afterFuncDecl(FunctionDeclaration decl) throws ASTTraversalException {}

    @Override
    public void visitFuncDecl(FunctionDeclaration decl) throws ASTTraversalException {
        this.beforeFuncDecl(decl);
        for (final Parameter formal : decl.getFormalParameters()) {
            formal.visit(this);
        }
        decl.getExpression().visit(this);
        decl.getName().visit(this);
        this.afterFuncDecl(decl);
    }

    @Override
    public void beforeVarOrCall(VarOrCall call) throws ASTTraversalException {
    }

    @Override
    public void afterVarOrCall(VarOrCall call) throws ASTTraversalException {
    }

    @Override
    public void visitVarOrCall(VarOrCall call) throws ASTTraversalException {
        this.beforeVarOrCall(call);
        call.getIdentifier().visit(this);
        
    }

    @Override
    public void beforeHardCoded(HardcodedExpression hc) throws ASTTraversalException {}

    @Override
    public void afterHardCoded(HardcodedExpression hc) throws ASTTraversalException {}

    @Override
    public void visitHardCoded(HardcodedExpression hc) throws ASTTraversalException {
        this.beforeHardCoded(hc);
        this.afterHardCoded(hc);
    }
}
