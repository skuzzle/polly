package de.skuzzle.polly.core.parser.ast.visitor.resolving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.ListType;
import de.skuzzle.polly.core.parser.ast.declarations.types.MapType;
import de.skuzzle.polly.core.parser.ast.declarations.types.ProductType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Empty;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.DepthFirstVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Unparser;
import de.skuzzle.polly.core.parser.util.Combinator;
import de.skuzzle.polly.core.parser.util.Combinator.CombinationCallBack;


/**
 * This visitor resolves <b>all</b> possible types for an expression and stores them in
 * each expression's <i>types</i> attribute. A Second pass type resolval is needed to 
 * determine each expression's unique type.
 * 
 * @author Simon Taddiken
 * @see SecondPassTypeResolver
 */
class FirstPassTypeResolver extends AbstractTypeResolver {
    
    
    public FirstPassTypeResolver(Namespace namespace, ProblemReporter reporter) {
        super(namespace, reporter);
    }
    
    
    
    @Override
    public int before(Native hc) throws ASTTraversalException {
        hc.resolveType(this.nspace, this);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Declaration decl) throws ASTTraversalException {
        decl.getExpression().visit(this);
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(final FunctionLiteral func) throws ASTTraversalException {
        switch (this.before(func)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        final List<Type> source = new ArrayList<Type>();
        
        // resolve parameter types
        this.enter();
        final Set<String> names = new HashSet<String>();
        for (final Declaration d : func.getFormal()) {
            if (!names.add(d.getName().getId())) {
                this.reportError(d.getName(),
                    "Doppelte Deklaration von '" + d.getName().getId() + "'");
            }
            if (!d.visit(this)) {
                return false;
            }
            source.add(d.getType());
            
            this.nspace.declare(d);
        }
        
        if (!func.getBody().visit(this)) {
            return false;
        }
        this.leave();
        
        // check whether all formal parameters have been used
        for (final Declaration d : func.getFormal()) {
            if (d.isUnused()) {
                // this.reportError(d, "Unbenutzer Parameter: " + d.getName());
            }
        }
        
        for (final Type te : func.getBody().getTypes()) {
            func.addType(new ProductType(source).mapTo(te));
        }
        
        return this.after(func) == CONTINUE;
    }
    
    
    
    @Override
    public int after(ListLiteral list) throws ASTTraversalException {
        if (list.getContent().isEmpty()) {
            this.reportError(list, "Listen müssen mind. 1 Element enthalten.");
        }
        for (final Expression exp : list.getContent()) {
            for (final Type t : exp.getTypes()) {
                list.addType(new ListType(t));
            }
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(final ProductLiteral product) throws ASTTraversalException {
        
        // Use combinator to create all combinations of possible types
        final CombinationCallBack<Expression, Type> ccb = 
            new CombinationCallBack<Expression, Type>() {

            @Override
            public List<Type> getSubList(Expression outer) {
                return outer.getTypes();
            }

            
            @Override
            public void onNewCombination(List<Type> combination) {
                product.addType(new ProductType(combination));
            }
        };
        
        Combinator.combine(product.getContent(), ccb);
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(final Assignment assign) throws ASTTraversalException {
        if (assign.isTemp()) {
            this.reportError(assign, 
                "Temporäre Deklarationen werden (noch?) nicht unterstützt.");
        }
        
        // deep transitive recursion check
        assign.getExpression().visit(new DepthFirstVisitor() {
            @Override
            public int before(VarAccess access) throws ASTTraversalException {
                final Collection<Declaration> decls = 
                    nspace.lookupAll(access.getIdentifier());
                
                for (final Declaration decl : decls) {
                    if (decl.getName().equals(assign.getName())) {
                        reportError(access, "Rekursive Aufrufe sind nicht erlaubt");
                    }
                    if (!decl.isNative()) {
                        if (!decl.getExpression().visit(this)) {
                            return ABORT;
                        }
                    }
                }
                return CONTINUE;
            } 
        });
        
        for (final Type t : assign.getExpression().getTypes()) {
            final Declaration vd = new Declaration(assign.getName().getPosition(), 
                assign.getName(), new Empty(t, assign.getExpression().getPosition()));
            this.nspace.declare(vd);
            
            assign.addType(t);
        }
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(OperatorCall call) throws ASTTraversalException {
        return this.visit((Call)call);
    }
    

    
    @Override
    public boolean visit(Call call) throws ASTTraversalException {
        switch (this.before(call)) {
        case SKIP: return true;
        case ABORT: return false;
        }
        
        // resolve parameter types
        if (!call.getRhs().visit(this)) {
            return false;
        }
        
        final List<Type> possibleTypes = new ArrayList<Type>(
            call.getRhs().getTypes().size());
        for (final Type rhsType : call.getRhs().getTypes()) {
            possibleTypes.add(rhsType.mapTo(Type.newTypeVar()));
        }
        
        // resolve called function's types
        if (!call.getLhs().visit(this)) {
            return false;
        }
        
        boolean hasMapType = false;
        for (final Type type : call.getLhs().getTypes()) {
            hasMapType |= type instanceof MapType;
        }
        if (!hasMapType) {
            this.reportError(call.getLhs(), "Unbekannte Funktion: " + 
                Unparser.toString(call.getLhs()));
        }
        
        if (call.getLhs().getTypes().isEmpty()) {
            this.reportError(call.getLhs(), "Funktion nicht gefunden");
        }
        
        // sort out all lhs types that do not match the rhs types
        for (final Type possibleLhs : possibleTypes) {
            for (final Type lhs : call.getLhs().getTypes()) {
                final Substitution subst = Type.unify(lhs, possibleLhs);
                if (subst != null) {
                    final MapType mtc = (MapType) lhs.subst(subst);
                    call.addType(mtc.getTarget());
                }
            }
        }
        
        
        if (call.getTypes().isEmpty()) {
            this.reportError(call.getRhs(),
                "Keine passende Deklaration für den Aufruf von " + 
                Unparser.toString(call.getLhs()) + " gefunden");
        }
        return this.after(call) == CONTINUE;
    }
    
    
    
    @Override
    public int before(VarAccess access) throws ASTTraversalException {
        final Set<Type> types = this.nspace.lookupFresh(access);
        access.addTypes(types);
        return CONTINUE;
    }
    
    
    
    @Override
    public boolean visit(NamespaceAccess access) throws ASTTraversalException {
        switch (this.before(access)) {
        case SKIP: return true;
        case ABORT: return false;
        }        
        if (!(access.getLhs() instanceof VarAccess)) {
            this.reportError(access.getLhs(), "Operand muss ein Bezeichner sein");
        } else if (!(access.getRhs() instanceof VarAccess)) {
            this.reportError(access.getRhs(), "Operand muss ein Bezeichner sein");
        }
        
        final VarAccess va = (VarAccess) access.getLhs();
        if (!Namespace.exists(va.getIdentifier())) {
            this.reportError(access.getLhs(), 
                "Unbekannter Namespace: " + va.getIdentifier());
        }
        final Namespace last = this.nspace;
        this.nspace = Namespace.forName(va.getIdentifier()).derive(this.nspace);
        if (!access.getRhs().visit(this)) {
            return false;
        }
        this.nspace = last;

        access.addTypes(access.getRhs().getTypes());
        
        return this.after(access) == CONTINUE;
    }
    
    
    
    @Override
    public int before(Delete delete) throws ASTTraversalException {
        delete.addType(Type.NUM);
        delete.setUnique(Type.NUM);
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Inspect inspect) throws ASTTraversalException {
        Namespace target = null;
        ResolvableIdentifier var = null;
        
        if (inspect.getAccess() instanceof VarAccess) {
            final VarAccess va = (VarAccess) inspect.getAccess();
            
            target = this.nspace;
            var = va.getIdentifier();
            
        } else if (inspect.getAccess() instanceof NamespaceAccess) {
            final NamespaceAccess nsa = (NamespaceAccess) inspect.getAccess();
            final VarAccess nsName = (VarAccess) nsa.getLhs();
            
            if (!Namespace.exists(nsName.getIdentifier())) {
                this.reportError(nsName, "Unbekannter Namespace: " + 
                    nsName.getIdentifier());
            }
            
            var = ((VarAccess) nsa.getRhs()).getIdentifier();
            target = Namespace.forName(nsName.getIdentifier());
        } else {
            throw new IllegalStateException("this should not be reachable");
        }
        
        final Collection<Declaration> decls = target.lookupAll(var);
        if (decls.isEmpty()) {
            this.reportError(var, "Unbekannte Variable: " + var);
        }
        inspect.setUnique(Type.STRING);
        inspect.addType(Type.STRING);
        return CONTINUE;
    }
}
