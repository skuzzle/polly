package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.Delete;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;

/**
 * {@link Visitor} implementation that traverses the AST in depth-first order. 
 * Implementation of <code>beforeXY</code> and <code>afterXY</code> methods is empty and
 * may be overridden by sub classes.
 * 
 * @author Simon Taddiken
 */
public class DepthFirstVisitor extends VisitorAdapter {
    
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
    public void visitLiteral(Literal literal) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeLiteral(literal);
        this.afterLiteral(literal);
    }

    
    
    @Override
    public void visitIdentifier(Identifier identifier)  throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeIdentifier(identifier);
        this.afterIdentifier(identifier);
    }
    
    
    
    @Override
    public void visitResolvable(ResolvableIdentifier id) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeResolvable(id);
        this.afterResolvable(id);
    }
    
    
    
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
    public void visitDecl(Declaration decl) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeDecl(decl);
        this.afterDecl(decl);
    }
    
    

    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeCall(call);
        call.getLhs().visit(this);
        call.getRhs().visit(this);
        this.afterCall(call);
    }

    

    @Override
    public void visitNative(Native hc) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeNative(hc);
        this.afterNative(hc);
    }

    

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
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeVarAccess(access);
        access.getIdentifier().visit(this);
        this.afterVarAccess(access);
    }


    
    @Override
    public void visitFunctionLiteral(FunctionLiteral func)
            throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeFunctionLiteral(func);
        for (final Declaration d : func.getFormal()) {
            d.visit(this);
        }
        func.getExpression().visit(this);
        this.afterFunctionLiteral(func);
    }


    
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
    public void visitProductLiteral(ProductLiteral product) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeProductLiteral(product);
        for (final Expression exp : product.getContent()) {
            exp.visit(this);
        }
        this.afterProductLiteral(product);
    }


    
    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeOperatorCall(call);
        call.getLhs().visit(this);
        call.getRhs().visit(this);
        this.afterOperatorCall(call);
    }
    
    
    
    @Override
    public void visitBraced(Braced braced) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeBraced(braced);
        braced.getExpression().visit(this);
        this.afterBraced(braced);
    }
    
    
    
    @Override
    public void visitDelete(Delete delete) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeDelete(delete);
        for (final Identifier id : delete.getIdentifiers()) {
            id.visit(this);
        }
        this.afterDelete(delete);
    }
}
