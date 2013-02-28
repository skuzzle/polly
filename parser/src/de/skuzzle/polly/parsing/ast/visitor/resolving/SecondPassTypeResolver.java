package de.skuzzle.polly.parsing.ast.visitor.resolving;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.types.MapType;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductType;
import de.skuzzle.polly.parsing.ast.declarations.types.Substitution;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Inspect;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;



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
    public boolean visit(Root root) throws ASTTraversalException {
        switch (this.before(root)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        for (final Expression exp : root.getExpressions()) {
            // check whether unique type could have been resolved
            if (!exp.typeResolved()) {
                //this.applyType(exp, exp);
            }
            if (!exp.visit(this)) {
                return false;
            }
        }
        
        return this.after(root) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(FunctionLiteral func) throws ASTTraversalException {
        switch (this.before(func)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        this.applyType(func, func);
        
        final MapType mtc = (MapType) func.getUnique();
        func.getBody().setUnique(mtc.getTarget());
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
        Type last = null;
        for (final Expression exp : list.getContent()) {
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
        
        if (list.getTypes().size() == 1) {
            list.setUnique(last.listOf());
        } else {
            this.reportError(list, "Uneindeutiger Listen Type");
        }
        
        return this.after(list) == CONTINUE;
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
    public int before(Assignment assign) throws ASTTraversalException {
        this.applyType(assign, assign.getExpression());
        return CONTINUE;
    }
    

    
    @Override
    public int after(Assignment assign) throws ASTTraversalException {
        this.applyType(assign, assign);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(OperatorCall call) throws ASTTraversalException {
        this.before((Call) call);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(VarAccess access) throws ASTTraversalException {
        this.applyType(access, access);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Call call) throws ASTTraversalException {
        
        // Either:
        // * call's unique type is already resolved => that is the final result type
        // * call has only one possible type => that is the final result type
        // * call has multiple possible types => result type is a TypeVar and must be 
        //       resolved.
        
        Type t;
        if (!call.typeResolved()) {
            if (call.getTypes().size() == 1) {
                t = call.getTypes().get(0);
            } else {
                t = Type.newTypeVar();
            }
        } else {
            t = call.getUnique();
        }
        
        
        // all call types that match a signature are stored in this list. If type 
        // resolution was successful, it will contain a single type.
        final Collection<Type> matched = new ArrayList<Type>();
        
        for (final Type s : call.getRhs().getTypes()) {
            final MapType tmp = new MapType(
                (ProductType) s, t);
            
            for (final Type lhsType : call.getLhs().getTypes()) {
                final Substitution subst = Type.unify(lhsType, tmp);
                if (subst != null) {
                    matched.add(lhsType.subst(subst));
                }
            }
        }
        
        if (matched.isEmpty()) {
            // no matching type found
            this.reportError(call.getLhs(), 
                "Keine passende Deklaration für den Aufruf von " + 
                Unparser.toString(call.getLhs()) + " gefunden");
        } else if (matched.size() != 1) {
            this.ambiguousCall(call, matched);
        }

        final MapType mtc = (MapType) matched.iterator().next();
        
        call.getRhs().setUnique(mtc.getSource());
        call.getLhs().setUnique(mtc);
        call.setUnique(t);
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        this.applyType(access, access.getRhs());
        return this.after(access) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect inspect) throws ASTTraversalException {
        // nothing to do here but prevent from executing super class visitInspect
        return this.before(inspect) == CONTINUE && this.after(inspect) == CONTINUE;
    }
}