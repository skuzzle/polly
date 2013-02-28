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
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;

/**
 * {@link ASTVisitor} implementation that traverses the AST in depth-first order. 
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
    public void visit(Root root) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(root);
        for (final Expression exp : root.getExpressions()) {
            exp.visit(this);
        }
        this.after(root);
    }

    
    
    @Override
    public void visit(Literal literal) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(literal);
        this.after(literal);
    }

    
    
    @Override
    public void visit(Identifier identifier)  throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(identifier);
        this.after(identifier);
    }
    
    
    
    @Override
    public void visit(ResolvableIdentifier id) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(id);
        this.after(id);
    }
    
    
    
    @Override
    public void visit(Assignment assign) 
            throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(assign);
        assign.getExpression().visit(this);
        assign.getName().visit(this);
        this.after(assign);
    }

    

    @Override
    public void visit(Declaration decl) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(decl);
        this.after(decl);
    }
    
    

    @Override
    public void visit(Call call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(call);
        call.getLhs().visit(this);
        call.getRhs().visit(this);
        this.after(call);
    }

    

    @Override
    public void visit(Native hc) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(hc);
        this.after(hc);
    }

    

    @Override
    public void visit(NamespaceAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(access);
        access.getLhs().visit(this);
        access.getRhs().visit(this);
        this.after(access);
    }


    
    @Override
    public void visit(VarAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(access);
        access.getIdentifier().visit(this);
        this.after(access);
    }


    
    @Override
    public void visit(FunctionLiteral func)
            throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(func);
        for (final Declaration d : func.getFormal()) {
            d.visit(this);
        }
        func.getBody().visit(this);
        this.after(func);
    }


    
    @Override
    public void visit(ListLiteral list) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(list);
        for (final Expression exp : list.getContent()) {
            exp.visit(this);
        }
        this.after(list);
    }
    
    
    
    @Override
    public void visit(ProductLiteral product) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(product);
        for (final Expression exp : product.getContent()) {
            exp.visit(this);
        }
        this.after(product);
    }


    
    @Override
    public void visit(OperatorCall call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(call);
        call.getLhs().visit(this);
        call.getRhs().visit(this);
        this.after(call);
    }
    
    
    
    @Override
    public void visit(Braced braced) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(braced);
        braced.getExpression().visit(this);
        this.after(braced);
    }
    
    
    
    @Override
    public void visit(Delete delete) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(delete);
        for (final Identifier id : delete.getIdentifiers()) {
            id.visit(this);
        }
        this.after(delete);
    }
    
    
    
    @Override
    public void visit(Inspect inspect) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.before(inspect);
        inspect.getAccess().visit(this);
        this.after(inspect);
    }
}
