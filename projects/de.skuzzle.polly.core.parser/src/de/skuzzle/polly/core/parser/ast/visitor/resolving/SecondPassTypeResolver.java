package de.skuzzle.polly.core.parser.ast.visitor.resolving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.core.parser.ParserProperties;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.types.ListType;
import de.skuzzle.polly.core.parser.ast.declarations.types.MapType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;
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
import de.skuzzle.polly.core.parser.problems.Problems;



class SecondPassTypeResolver extends AbstractTypeResolver {

    
    
    public SecondPassTypeResolver(FirstPassTypeResolver fptr) {
        super(fptr.getCurrentNameSpace(), fptr.reporter);
    }
    
    
    
    private boolean applyType(Expression child) 
            throws ASTTraversalException {
        if (child.getTypes().size() == 1 && !child.typeResolved()) {
            child.setUnique(child.getTypes().get(0));
            return true;
        } else if (!child.typeResolved() && !this.reporter.hasProblems()) {
            this.reportError(child, "Nicht eindeutiger Typ");
        }
        return child.typeResolved();
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
        
        if (!this.applyType(node)) {
            return false;
        }

        
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
        
        if (node.getContent().isEmpty()) {
            // empty lists are not allowed. This is checked by FirstPassTypeResolver
            return true;
        }
        
        if (!this.applyType(node)) {
            return false;
        }
        
        final Type expected = ((ListType) node.getUnique()).getSubType();
        
        for (final Expression exp : node.getContent()) {
            exp.setUnique(expected);
            if (!exp.visit(this)) {
                return false;
            }
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
    public boolean visit(Assignment node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        
        if (ParserProperties.should(ParserProperties.ALLOW_POLYMORPHIC_DECLS)) {
            if (node.getExpression() instanceof FunctionLiteral) {
                final FunctionLiteral fun = (FunctionLiteral) node.getExpression();
                final List<Type> types = new ArrayList<>(fun.getFormal().size());
                boolean ispoly = false;
                for (final Declaration d : fun.getFormal()) {
                    ispoly |= d.getType() instanceof TypeVar;
                    types.add(d.getType());
                }
                
                if (ispoly) {
                    this.reportError(node, Problems.ASSIGNMENT_NOT_ALLOWED);
                }
            }
        }
        
        if (!this.applyType(node)) {
            return false;
        }
        if (!node.getExpression().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public int before(OperatorCall node) throws ASTTraversalException {
        this.before((Call) node);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(VarAccess node) throws ASTTraversalException {
        this.applyType(node);
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
        final Collection<MapType> matched = new ArrayList<MapType>();
        
        for (final Type s : node.getRhs().getTypes()) {
            final MapType tmp = s.mapTo(t);
            
            for (final Type lhsType : node.getLhs().getTypes()) {
                final Substitution subst = Type.unify(tmp, lhsType);
                if (subst != null) {
                    // safe cast because unification was successful
                    // and lhs must be a MapType
                    matched.add((MapType) lhsType.subst(subst));
                }
            }
        }
        
        MapType mtc = null;
        if (matched.isEmpty()) {
            // no matching type found
            final String problem = node instanceof OperatorCall 
                ? Problems.INCOMPATIBLE_OP 
                : Problems.INCOMPATIBLE_CALL;
            this.reportError(node.getLhs(), problem, Unparser.toString(node.getLhs()));
            return CONTINUE;
        } else if (matched.size() != 1) {
            mtc = Type.getMostSpecific(matched);
            if (mtc == null) {
                this.reportError(node, Problems.AMBIGUOUS_CALL);
                return CONTINUE;
            }
        } else {
            mtc = (MapType) matched.iterator().next();
        }

        
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
        if (!this.applyType(node)) {
            return false;
        }
        if (!node.getRhs().visit(this)) {
            return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Inspect node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        // nothing to do here but prevent from executing super class visitInspect
        return this.after(node) == CONTINUE;
    }
}