package de.skuzzle.polly.core.parser.ast.visitor.resolving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.core.parser.ParserProperties;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.ListType;
import de.skuzzle.polly.core.parser.ast.declarations.types.MapType;
import de.skuzzle.polly.core.parser.ast.declarations.types.ProductType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Empty;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.core.parser.ast.visitor.ASTRewrite;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.CopyTransformation;
import de.skuzzle.polly.core.parser.ast.visitor.ForEachTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.Unparser;
import de.skuzzle.polly.core.parser.problems.ProblemReporter;
import de.skuzzle.polly.core.parser.problems.Problems;



class SecondPassTypeResolver extends AbstractTypeResolver {

    private final ASTRewrite rewrite;
    
    
    public SecondPassTypeResolver(Namespace nspace, ProblemReporter reporter) {
        super(nspace, reporter);
        this.rewrite = new ASTRewrite();
    }
    
    
    
    public ASTRewrite getRewrite() {
        return this.rewrite;
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
    
    
    
    private boolean propagateDown(Expression target, Type type) 
            throws ASTTraversalException {
        Type last = null;
        boolean matches = false;
        for (final Type t : target.getTypes()) {
            last = t;
            matches |= Type.tryUnify(type, t);
        }
        if (!matches) {
            this.typeError(target, type, last);
            return false;
        }
        target.setUnique(type);
        return true;
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
        
        this.enter();
        
        final MapType mtc = (MapType) node.getUnique();
        final Iterator<Type> typeIt = ((ProductType) mtc.getSource()).getTypes().iterator();
        for (final Declaration formal : node.getFormal()) {
            this.nspace.declare(new Declaration(formal.getPosition(), formal.getName(), 
                new Empty(typeIt.next(), formal.getPosition()), true));
        }
        if (!this.propagateDown(node.getBody(), mtc.getTarget()) || 
            !node.getBody().visit(this)) {
            
            return false;
        }
        
        this.leave();
        
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
            if (!this.propagateDown(exp, expected) || !exp.visit(this)) {
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
        
        if (!this.applyType(node)) {
            return false;
        }
        if (node.getContent().isEmpty()) {
            node.setUnique(new ProductType(Type.VOID));
            return true;
        }
        
        final ProductType t = (ProductType) node.getUnique();
        final Iterator<Type> typeIt = t.getTypes().iterator();
        final Iterator<Expression> expIt = node.getContent().iterator();
        
        while (typeIt.hasNext()) {
            final Expression exp = expIt.next();
            final Type type = typeIt.next();
            
            if (!this.propagateDown(exp, type) || !exp.visit(this)) {
                return false;
            }
        }
        
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(Assignment node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        
        if (ParserProperties.should(ParserProperties.ALLOW_POLYMORPHIC_DECLS)) {
            if (node.getExpression() instanceof FunctionLiteral) {
                // if we have a polymorphic declaration, we do not want to use its
                // current type (which would be bound to this context) but its generic
                // declared type instead.
                
                final FunctionLiteral fun = (FunctionLiteral) node.getExpression();
                final List<Type> types = new ArrayList<>(fun.getFormal().size());
                boolean ispoly = false;
                for (final Declaration d : fun.getFormal()) {
                    ispoly |= Type.containsTypeVar(d.getType());
                    types.add(d.getType());
                }
                
                if (ispoly) {
                    final Type signature = new ProductType(types).mapTo(Type.newTypeVar());
                    node.setUnique(signature);
                    this.propagateDown(node.getExpression(), signature);
                    return true;
                }
            }
        }
        
        if (!this.applyType(node) || 
            !this.propagateDown(node.getExpression(), node.getUnique()) || 
            !node.getExpression().visit(this)) {
            
            return false;
        }
        return this.after(node) == CONTINUE;
    }
    
    
    
    @Override
    public int before(OperatorCall node) throws ASTTraversalException {
        return this.before((Call) node);
    }
    
    
    
    @Override
    public int before(VarAccess node) throws ASTTraversalException {
        this.applyType(node);
        
        if (node.getParent() instanceof Call) {
            // this is LHS of a call, so we must infer type of the accessed variable in
            // context of that call
            final Call parent = (Call) node.getParent();
            
            final Declaration target = this.nspace.tryResolve(node.getIdentifier(), 
                node.getUnique());
            
            // no need for native declarations
            if (target.isNative()) {
                return CONTINUE;
            }
            
            // construct temporary copy that can be used to determine the type
            final Expression copy = target.getExpression().transform(new CopyTransformation());
            final ProductLiteral rhsCopy = parent.getRhs().transform(new CopyTransformation());
            final Call newCall = new Call(node.getPosition(), copy, rhsCopy);
            // reset all types
            copy.traverse(new ForEachTraversal() {
                @Override
                protected int beforeEach(Node node) {
                    if (node instanceof Literal) {
                        // preserve literal's types
                    } else if (node instanceof Expression) {
                        final Expression exp = (Expression) node;
                        exp.setUnique(Type.UNKNOWN);
                        exp.setTypes(Collections.<Type>emptyList());
                    }
                    return CONTINUE;
                }
            });
            
            // resolve new types in current context
            final FirstPassTypeResolver fptr = new FirstPassTypeResolver(this.nspace, 
                this.reporter.subReporter(node.getPosition()));
            newCall.visit(fptr);
            
            if (node.getUnique() instanceof MapType) {
                final MapType mapUnique = (MapType) node.getUnique();
                copy.setUnique(mapUnique);
                newCall.setUnique(mapUnique.getTarget());
            } else {
                copy.setUnique(node.getUnique());
            }
            final SecondPassTypeResolver sptr = new SecondPassTypeResolver(this.nspace, 
                this.reporter.subReporter(node.getPosition()));
            newCall.visit(sptr);
            
            final Substitution s = Type.unify(newCall.getLhs().getUnique(), 
                node.getUnique());
            
            node.setUnique(node.getUnique().subst(s));
            newCall.setUnique(newCall.getUnique().subst(s));
            parent.setUnique(newCall.getUnique());
            
            // create fake declaration
            final Declaration decl = new Declaration(node.getPosition(), 
                node.getIdentifier(), newCall.getLhs());
            node.getIdentifier().setDeclaration(decl);
        }
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
        
        // check if node has a constraint on that return type
        final Substitution constraint = node.getConstraint(t);
        
        
        // all call types that match a signature are stored in this list. If type 
        // resolution was successful, it will contain a single type.
        final Collection<MapType> matched = new ArrayList<MapType>();
        
        for (final Type s : node.getRhs().getTypes()) {
            MapType tmp = s.mapTo(t);

            // if the result type has constraints, those must be applied to the possible 
            // unique type so that they are obeyed
            if (constraint != null) {
                tmp = (MapType) tmp.subst(constraint);
            }
            
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
        node.setUnique(mtc.getTarget());
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess node) throws ASTTraversalException {
        switch (this.before(node)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        if (!this.applyType(node) || 
            this.propagateDown(node.getRhs(), node.getUnique()) || 
            !node.getRhs().visit(this)) {
            
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