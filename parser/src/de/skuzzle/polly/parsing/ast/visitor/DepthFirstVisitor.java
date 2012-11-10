package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.FunctionParameter;
import de.skuzzle.polly.parsing.ast.declarations.ListParameter;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Hardcoded;
import de.skuzzle.polly.parsing.ast.expressions.LambdaCall;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
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
    public void beforeResolvable(ResolvableIdentifier id) throws ASTTraversalException {}

    @Override
    public void afterResolvable(ResolvableIdentifier id) throws ASTTraversalException {}

    @Override
    public void visitResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        this.beforeResolvable(id);
        this.afterResolvable(id);
    }
    
    
    
    @Override
    public void beforeAssignment(Assignment assign)
        throws ASTTraversalException {}

    @Override
    public void afterAssignment(Assignment assign)
        throws ASTTraversalException {}

    @Override
    public void visitAssignment(Assignment assign) 
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
    public void beforeListParameter(ListParameter param) throws ASTTraversalException {}

    @Override
    public void afterListParameter(ListParameter param) throws ASTTraversalException {}

    @Override
    public void visitListParameter(ListParameter param) throws ASTTraversalException {
        this.beforeListParameter(param);
        this.afterListParameter(param);
    }
    
    
    
    @Override
    public void beforeFunctionParameter(FunctionParameter param) 
            throws ASTTraversalException {}

    @Override
    public void afterFunctionParameter(FunctionParameter param) 
            throws ASTTraversalException {}

    @Override
    public void visitFunctionParameter(FunctionParameter param) 
            throws ASTTraversalException {
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
        this.afterVarDecl(decl);
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
    public void beforeHardCoded(Hardcoded hc) throws ASTTraversalException {}

    @Override
    public void afterHardCoded(Hardcoded hc) throws ASTTraversalException {}

    @Override
    public void visitHardCoded(Hardcoded hc) throws ASTTraversalException {
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
        for (final Expression exp : call.getParameters()) {
            exp.visit(this);
        }
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

    @Override
    public void beforeFunctionLiteral(FunctionLiteral func)
        throws ASTTraversalException {}

    @Override
    public void afterFunctionLiteral(FunctionLiteral func)
        throws ASTTraversalException {}

    @Override
    public void visitFunctionLiteral(FunctionLiteral func)
            throws ASTTraversalException {
        this.beforeFunctionLiteral(func);
        func.getExpression().visit(this);
        this.afterFunctionLiteral(func);
    }

    @Override
    public void beforeListLiteral(ListLiteral list) throws ASTTraversalException {}

    @Override
    public void afterListLiteral(ListLiteral list) throws ASTTraversalException {}

    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        this.beforeListLiteral(list);
        for (final Expression exp : list.getContent()) {
            exp.visit(this);
        }
        this.afterListLiteral(list);
    }

    @Override
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException {}

    @Override
    public void afterOperatorCall(OperatorCall call) throws ASTTraversalException {}

    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeOperatorCall(call);
        call.getIdentifier().visit(this);
        for (final Expression exp : call.getParameters()) {
            exp.visit(this);
        }
        this.afterOperatorCall(call);
    }
}
