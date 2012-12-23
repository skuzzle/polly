package de.skuzzle.polly.parsing.ast.visitor.resolving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


class SecondPassTypeResolver extends AbstractTypeResolver {

    
    
    
    public SecondPassTypeResolver(FirstPassTypeResolver fptr) {
        super(fptr.getCurrentNameSpace());
    }
    
    
    
    private void applyType(Expression parent, Expression child) 
            throws ASTTraversalException {
        if (parent.getTypes().size() == 1 && !child.typeResolved()) {
            child.setUnique(parent.getTypes().get(0));
        } else {
            this.reportError(parent, "Nicht eindeutiger Typ für Ausdruck.");
        }
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
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeListLiteral(list);
        
        for (final Expression exp : list.getContent()) {
            exp.visit(this);
            if (!Type.unify(list.getUnique(), new ListTypeConstructor(exp.getUnique()))) {
                final ListTypeConstructor lt = (ListTypeConstructor) list.getUnique();
                this.typeError(exp, lt.getSubType(), exp.getUnique());
            }
        }
        
        this.afterListLiteral(list);
    }
    
    
    
    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {
    }
    
    
    
    @Override
    public void beforeAssignment(Assignment assign) throws ASTTraversalException {
        this.applyType(assign, assign.getExpression());
    }
    
    
    
    @Override
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeCall(call);
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
        
        
        // Unify the call's possible types against all possible actual signature types
        // to find the one correct type. Multiple or no matches indicate errors.
        MapTypeConstructor mtc = null;
        
        // all call types that match a signature are stored in this list. If type 
        // resolution was successful, it will contain a single type.
        final Collection<Type> matched = new ArrayList<Type>();
        for (final ProductTypeConstructor s : call.getSignatureTypes()) {
            final MapTypeConstructor tmp = new MapTypeConstructor(s, t);
            
            for (final Type lhsType : call.getLhs().getTypes()) {
                if (Type.unify(lhsType, tmp)) {
                    mtc = tmp;
                    matched.add(mtc);
                }
            }
        }
        
        if (matched.isEmpty()) {
            // no matching type found
            this.reportError(call, 
                "Keine passende Deklaration für den Aufruf gefunden");
        } else if (matched.size() != 1) {
            this.ambiguosCall(call, matched);
        }

        call.getLhs().setUnique(mtc);
        call.setUnique(t);
        
        // Set unique types of the actual parameters according to the single resolved
        // signature.
        // invariant: mtc.source.size == call.getParameters.size
        final Iterator<Type> uniqueIt = mtc.getSource().iterator();
        for (final Expression exp : call.getParameters()) {
            exp.setUnique(uniqueIt.next());
        }
    }
    
    
    
    @Override
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException {
        this.applyType(access, access.getRhs());
    }
}