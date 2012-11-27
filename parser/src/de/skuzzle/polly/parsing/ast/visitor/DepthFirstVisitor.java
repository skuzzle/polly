package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.FunctionParameter;
import de.skuzzle.polly.parsing.ast.declarations.ListParameter;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Hardcoded;
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
    
    /** Flag to determine if current traversal should be stopped */
    protected boolean aborted;
    
    
    
    /**
     * Aborts the current traversal process. All further <code>visitXXX</code> methods 
     * will return immediately without traversing their children after <code>abort</code> 
     * has been called. This behavior will be leveraged when overriding any of the 
     * <code>visitXXX</code> methods and must thus be reimplemented by querying the 
     * {@link #aborted} flag.
     */
    public void abort() {
        this.aborted = true;
    }
    
    

    @Override
    public void beforeRoot(Root root) throws ASTTraversalException {}

    @Override
    public void afterRoot(Root root) throws ASTTraversalException {}

    @Override
    public void visitRoot(Root root) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
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
        if (this.aborted) {
            return;
        }
        this.beforeLiteral(literal);
        this.afterLiteral(literal);
    }

    
    
    @Override
    public void beforeIdentifier(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void afterIdentifier(Identifier identifier) throws ASTTraversalException {}

    @Override
    public void visitIdentifier(Identifier identifier)  throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeIdentifier(identifier);
        this.afterIdentifier(identifier);
    }
    
    
    
    @Override
    public void beforeResolvable(ResolvableIdentifier id) throws ASTTraversalException {}

    @Override
    public void afterResolvable(ResolvableIdentifier id) throws ASTTraversalException {}

    @Override
    public void visitResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
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
        if (this.aborted) {
            return;
        }
        this.beforeAssignment(assign);
        assign.getExpression().visit(this);
        assign.getName().visit(this);
        this.afterAssignment(assign);
    }

    
    
    @Override
    public void beforeParameter(Parameter param) throws ASTTraversalException {}

    @Override
    public void afterParameter(Parameter param) throws ASTTraversalException {}

    @Override
    public void visitParameter(Parameter param) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeParameter(param);
        this.afterParameter(param);
    }
    
    
    
    @Override
    public void beforeListParameter(ListParameter param) throws ASTTraversalException {}

    @Override
    public void afterListParameter(ListParameter param) throws ASTTraversalException {}

    @Override
    public void visitListParameter(ListParameter param) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
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
        if (this.aborted) {
            return;
        }
        this.beforeFunctionParameter(param);
        this.afterFunctionParameter(param);
    }

    
    
    @Override
    public void beforeVarDecl(VarDeclaration decl) throws ASTTraversalException {}

    @Override
    public void afterVarDecl(VarDeclaration decl) throws ASTTraversalException {}

    @Override
    public void visitVarDecl(VarDeclaration decl) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeVarDecl(decl);
        this.afterVarDecl(decl);
    }
    
    

    @Override
    public void beforeCall(Call call) throws ASTTraversalException {}

    @Override
    public void afterCall(Call call) throws ASTTraversalException {}

    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeCall(call);
        call.getLhs().visit(this);
        for (final Expression exp : call.getParameters()) {
            exp.visit(this);
        }
        this.afterCall(call);
    }

    
    
    @Override
    public void beforeHardcoded(Hardcoded hc) throws ASTTraversalException {}

    @Override
    public void afterHardcoded(Hardcoded hc) throws ASTTraversalException {}

    @Override
    public void visitHardcoded(Hardcoded hc) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeHardcoded(hc);
        this.afterHardcoded(hc);
    }

    
    
    @Override
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException {}

    @Override
    public void afterAccess(NamespaceAccess access) throws ASTTraversalException {}

    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeAccess(access);
        access.getLhs().visit(this);
        access.getRhs().visit(this);
        this.afterAccess(access);
    }

    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {}

    @Override
    public void afterVarAccess(VarAccess access) throws ASTTraversalException {}

    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
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
        if (this.aborted) {
            return;
        }
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
        if (this.aborted) {
            return;
        }
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
        if (this.aborted) {
            return;
        }
        this.beforeOperatorCall(call);
        call.getLhs().visit(this);
        for (final Expression exp : call.getParameters()) {
            exp.visit(this);
        }
        this.afterOperatorCall(call);
    }
}
