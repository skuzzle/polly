package de.skuzzle.polly.core.parser.ast.visitor.resolving;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.types.MapType;
import de.skuzzle.polly.core.parser.ast.declarations.types.ProductType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.Unparser;



class SecondPassTypeResolver extends AbstractTypeResolver {

    
    
    public SecondPassTypeResolver(FirstPassTypeResolver fptr) {
        super(fptr.getCurrentNameSpace(), fptr.getUnifier());
    }
    
    
    
    private void applyType(Expression parent, Expression child) 
            throws ASTTraversalException {
        if (parent.getTypes().size() == 1 && !child.typeResolved()) {
            child.setUnique(parent.getTypes().get(0));
            parent.setUnique(child.getUnique());
        } else if (!child.typeResolved()){
            this.reportError(parent, "Nicht eindeutiger Typ");
        }
    }
    
    
    
    @Override
    public boolean visit(Root node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        for (final Expression exp : node.getExpressions()) {
            // check whether unique type could have been resolved
            if (!exp.typeResolved()) {
                //this.applyType(exp, exp);
            }
            if (!exp.visit(this)) {
                return false;
            }
        }
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(FunctionLiteral node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.applyType(node, node);
        
        final MapType mtc = (MapType) node.getUnique();
        node.getBody().setUnique(mtc.getTarget());
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
        Type last = null;
        for (final Expression exp : node.getContent()) {
            if (!exp.visit(this)) {
                return false;
            }
            if (last != null) {
                if (!Type.tryUnify(last, exp.getUnique())) {
                    this.typeError(exp, last, exp.getUnique());
                }
            }
            last = exp.getUnique();
        }
        
        if (node.getTypes().size() == 1) {
            node.setUnique(last.listOf());
        } else {
            this.reportError(node, "Uneindeutiger Listen Type");
        }
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    /*@Override
    public void visitProductLiteral(ProductLiteral product) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeProductLiteral(product);
        
        for (final Expression exp : product.getContent()) {
            exp.visit(this);
        }
        
        this.afterProductLiteral(product);
    }*/
    
    
    
    @Override
    public int before(Assignment node) throws ASTTraversalException {
        this.applyType(node, node.getExpression());
        return CONTINUE;
    }
    

    
    @Override
    public int after(Assignment node) throws ASTTraversalException {
        this.applyType(node, node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(OperatorCall node) throws ASTTraversalException {
        this.before((Call) node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(VarAccess node) throws ASTTraversalException {
        this.applyType(node, node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Call node) throws ASTTraversalException {
        
        // Either:
        // * call's unique type is already resolved => that is the final result type
        // * call has only one possible type => that is the final result type
        // * call has multiple possible types => result type is a TypeVar and must be 
        //       resolved.
        
        Type t;
        if (!node.typeResolved()) {
            if (node.getTypes().size() == 1) {
                t = node.getTypes().get(0);
            } else {
                t = Type.newTypeVar();
            }
        } else {
            t = node.getUnique();
        }
        
        
        // all call types that match a signature are stored in this list. If type 
        // resolution was successful, it will contain a single type.
        final Collection<Type> matched = new ArrayList<Type>();
        
        for (final Type s : node.getRhs().getTypes()) {
            final MapType tmp = new MapType(
                (ProductType) s, t);
            
            for (final Type lhsType : node.getLhs().getTypes()) {
                final Substitution subst = Type.unify(lhsType, tmp);
                if (subst != null) {
                    matched.add(lhsType.subst(subst));
                }
            }
        }
        
        if (matched.isEmpty()) {
            // no matching type found
            this.reportError(node.getLhs(), 
                "Keine passende Deklaration für den Aufruf von " + 
                Unparser.toString(node.getLhs()) + " gefunden");
        } else if (matched.size() != 1) {
            this.ambiguousCall(node, matched);
        }

        final MapType mtc = (MapType) matched.iterator().next();
        
        node.getRhs().setUnique(mtc.getSource());
        node.getLhs().setUnique(mtc);
        node.setUnique(t);
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.applyType(node, node.getRhs());
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect node) throws ASTTraversalException {
        // nothing to do here but prevent from executing super class visitInspect
        return this.before(node) == CONTINUE && this.after(node) == CONTINUE;
    }
}