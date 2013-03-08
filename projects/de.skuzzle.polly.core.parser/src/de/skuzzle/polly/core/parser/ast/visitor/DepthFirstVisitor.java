package de.skuzzle.polly.core.parser.ast.visitor;

import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Braced;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;

/**
 * {@link ASTVisitor} implementation that traverses the AST in depth-first order. 
 * Implementation of <code>beforeXY</code> and <code>afterXY</code> methods is empty and
 * may be overridden by sub classes.
 * 
 * @author Simon Taddiken
 */
public class DepthFirstVisitor extends VisitorAdapter {
    
    
    @Override
    public boolean visit(Root root) throws ASTTraversalException {
        switch (this.before(root)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Expression exp : root.getExpressions()) {
            if (!exp.visit(this)) {
                return false;
            }
        }
        return this.after(root) == CONTINUE;
    }

    
    
    @Override
    public boolean visit(Literal literal) throws ASTTraversalException {
        return this.before(literal) == CONTINUE && this.after(literal) == CONTINUE;
    }

    
    
    @Override
    public boolean visit(Identifier identifier)  throws ASTTraversalException {
        return this.before(identifier) == CONTINUE && this.after(identifier) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ResolvableIdentifier id) throws ASTTraversalException {
        return this.before(id) == CONTINUE && this.after(id) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Assignment assign) throws ASTTraversalException {
        switch (this.before(assign)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!assign.getExpression().visit(this)) {
            return false;
        }
        if (!assign.getName().visit(this)) {
            return false;
        }
        return this.after(assign) == CONTINUE;
    }

    

    @Override
    public boolean visit(Declaration decl) throws ASTTraversalException {
        return this.before(decl) == CONTINUE && this.after(decl) == CONTINUE;
    }
    
    

    @Override
    public boolean visit(Call call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!call.getLhs().visit(this)) {
            return false;
        }
        if (!call.getRhs().visit(this)) {
            return false;
        }
        return this.after(call) == CONTINUE;
    }

    

    @Override
    public boolean visit(Native hc) throws ASTTraversalException {
        return this.before(hc) == CONTINUE && this.after(hc) == CONTINUE;    
    }

    

    @Override
    public boolean visit(NamespaceAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!access.getLhs().visit(this)) {
            return false;
        }
        if (!access.getRhs().visit(this)) {
            return false;
        }
        return this.after(access) == CONTINUE;
    }


    
    @Override
    public boolean visit(VarAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!access.getIdentifier().visit(this)) {
            return false;
        }
        return this.after(access) == CONTINUE;
    }


    
    @Override
    public boolean visit(FunctionLiteral func) throws ASTTraversalException {
        switch (this.before(func)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Declaration d : func.getFormal()) {
            if (!d.visit(this)) {
                return false;
            }
        }
        if (!func.getBody().visit(this)) {
            return false;
        }
        return this.after(func) == CONTINUE;
    }


    
    @Override
    public boolean visit(ListLiteral list) throws ASTTraversalException {
        switch (this.before(list)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Expression exp : list.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
        }
        return this.after(list) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ProductLiteral product) throws ASTTraversalException {
        switch (this.before(product)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Expression exp : product.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
        }
        return this.after(product) == CONTINUE;
    }


    
    @Override
    public boolean visit(OperatorCall call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!call.getLhs().visit(this)) {
            return false;
        }
        if (!call.getRhs().visit(this)) {
            return false;
        }
        return this.after(call) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Braced braced) throws ASTTraversalException {
        switch (this.before(braced)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!braced.getExpression().visit(this)) {
            return false;
        }
        return this.after(braced) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Delete delete) throws ASTTraversalException {
        switch (this.before(delete)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Identifier id : delete.getIdentifiers()) {
            if (!id.visit(this)) {
                return false;
            }
        }
        return this.after(delete) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect inspect) throws ASTTraversalException {
        switch (this.before(inspect)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!inspect.getAccess().visit(this)) {
            return false;
        }
        return this.after(inspect) == CONTINUE;
    }
}
