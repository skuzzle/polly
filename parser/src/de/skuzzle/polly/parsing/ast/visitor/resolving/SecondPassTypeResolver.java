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
    public void visitRoot(Root root) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeRoot(root);
        
        for (final Expression exp : root.getExpressions()) {
            // check whether unique type could have been resolved
            if (!exp.typeResolved()) {
                //this.applyType(exp, exp);
            }
            exp.visit(this);
        }
        
        this.afterRoot(root);
    }
    
    
    
    @Override
    public void visitFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeFunctionLiteral(func);
        
        this.applyType(func, func);
        
        final MapType mtc = (MapType) func.getUnique();
        func.getBody().setUnique(mtc.getTarget());
        func.getBody().visit(this);
        
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeListLiteral(list);
        
        Type last = null;
        for (final Expression exp : list.getContent()) {
            exp.visit(this);
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
        
        this.afterListLiteral(list);
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
    public void beforeAssignment(Assignment assign) throws ASTTraversalException {
        this.applyType(assign, assign.getExpression());
    }
    

    
    @Override
    public void afterAssignment(Assignment assign) throws ASTTraversalException {
        this.applyType(assign, assign);
    }
    
    
    
    @Override
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeCall(call);
    }
    
    
    
    @Override
    public void afterVarAccess(VarAccess access) throws ASTTraversalException {
        this.applyType(access, access);
    }
    
    
    
    @Override
    public void beforeCall(Call call) throws ASTTraversalException {
        
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
    }
    
    
    
    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        this.beforeAccess(access);
        this.applyType(access, access.getRhs());
        this.afterAccess(access);
    }
}