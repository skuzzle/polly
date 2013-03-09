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
    public boolean visit(Root node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Expression exp : node.getExpressions()) {
            if (!exp.visit(this)) {
                return false;
            }
        }
        return this.after(node) == CONTINUE;
    }

    
    
    @Override
    public boolean visit(Literal node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        return this.after(node) == CONTINUE;
    }

    
    
    @Override
    public boolean visit(Identifier node)  throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ResolvableIdentifier node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Assignment node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!node.getExpression().visit(this)) {
            return false;
        }
        if (!node.getName().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }

    

    @Override
    public boolean visit(Declaration node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    

    @Override
    public boolean visit(Call node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!node.getLhs().visit(this)) {
            return false;
        }
        if (!node.getRhs().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }

    

    @Override
    public boolean visit(Native node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        return this.after(node) == CONTINUE;
    }

    

    @Override
    public boolean visit(NamespaceAccess node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!node.getLhs().visit(this)) {
            return false;
        }
        if (!node.getRhs().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }


    
    @Override
    public boolean visit(VarAccess node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!node.getIdentifier().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }


    
    @Override
    public boolean visit(FunctionLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Declaration d : node.getFormal()) {
            if (!d.visit(this)) {
                return false;
            }
        }
        if (!node.getBody().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }


    
    @Override
    public boolean visit(ListLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Expression exp : node.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(ProductLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Expression exp : node.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
        }
        return this.after(node) == CONTINUE;
    }


    
    @Override
    public boolean visit(OperatorCall node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!node.getLhs().visit(this)) {
            return false;
        }
        if (!node.getRhs().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Braced node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!node.getExpression().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Delete node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        for (final Identifier id : node.getIdentifiers()) {
            if (!id.visit(this)) {
                return false;
            }
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!node.getAccess().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }
}
