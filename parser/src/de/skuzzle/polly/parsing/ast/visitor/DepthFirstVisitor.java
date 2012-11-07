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
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
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
    public void afterIdentifier(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void visitIdentifier(Identifier identifier)  throws ASTTraversalException {
        this.beforeIdentifier(identifier);
        this.afterIdentifier(identifier);
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
    public void beforeCall(Call call) throws ASTTraversalException {}

    @Override
    public void afterCall(Call call) throws ASTTraversalException {}

    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        this.beforeCall(call);
        call.getIdentifier().visit(this);
        for (final Expression exp : call.getParameters()) {
            exp.visit(this);
        }
        this.afterCall(call);
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

    
    
    @Override
    public void beforeLambdaCall(LambdaCall call) throws ASTTraversalException {}

    @Override
    public void afterLambdaCall(LambdaCall call) throws ASTTraversalException {}

    @Override
    public void visitLambdaCall(LambdaCall call) throws ASTTraversalException {
        this.beforeLambdaCall(call);
        call.getLambda().visit(this);
        this.afterLambdaCall(call);
    }

    
    
    @Override
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException {}

    @Override
    public void afterAccess(NamespaceAccess access) throws ASTTraversalException {}

    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        this.beforeAccess(access);
        access.getName().visit(this);
        access.getRhs().visit(this);
        this.afterAccess(access);
    }

    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {}

    @Override
    public void afterVarAccess(VarAccess access) throws ASTTraversalException {}

    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        this.beforeVarAccess(access);
        access.getIdentifier().visit(this);
        this.afterVarAccess(access);
    }
}
