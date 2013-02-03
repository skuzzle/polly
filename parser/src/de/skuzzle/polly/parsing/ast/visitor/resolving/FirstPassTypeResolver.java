package de.skuzzle.polly.parsing.ast.visitor.resolving;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListType;
import de.skuzzle.polly.parsing.ast.declarations.types.MapType;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductType;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeResolvedCallBack;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeUnifier;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Empty;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;
import de.skuzzle.polly.parsing.util.Combinator;
import de.skuzzle.polly.parsing.util.Combinator.CombinationCallBack;


/**
 * This visitor resolves <b>all</b> possible types for an expression and stores them in
 * each expression's <i>types</i> attribute. A Second pass type resolval is needed to 
 * determine each expression's unique type.
 * 
 * @author Simon Taddiken
 * @see SecondPassTypeResolver
 */
class FirstPassTypeResolver extends AbstractTypeResolver {
    
    
    public FirstPassTypeResolver(Namespace namespace) {
        super(namespace);
    }
    
    
    
    @Override
    public void beforeNative(Native hc) throws ASTTraversalException {
        hc.resolveType(this.nspace, this);
    }
    
    
    
    @Override
    public void beforeDecl(Declaration decl) throws ASTTraversalException {
        decl.getExpression().visit(this);
    }
    
    
    
    @Override
    public void visitFunctionLiteral(final FunctionLiteral func) 
            throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeFunctionLiteral(func);
        
        final List<Type> source = new ArrayList<Type>();
        
        // resolve parameter types
        this.enter();
        final Set<String> names = new HashSet<String>();
        for (final Declaration d : func.getFormal()) {
            if (!names.add(d.getName().getId())) {
                this.reportError(d.getName(),
                    "Doppelte Deklaration von '" + d.getName().getId() + "'");
            }
            d.visit(this);
            source.add(d.getType());
            
            this.nspace.declare(d, this.unifier);
        }
        
        // Whenever a type for the body of this function has been resolved, we have
        // found proper substitutions for the function's parameters
        func.getBody().setTypeResolvedCallBack(new TypeResolvedCallBack() {
            
            @Override
            public void typeResolved(Type possibleType, TypeUnifier context) {
                final Type t = new MapType(
                    new ProductType(source), possibleType);
                
                func.addType(context.substitute(t), context);
            }
        });
        
        func.getBody().visit(this);
        this.leave();
        
        // check whether all formal parameters have been used
        for (final Declaration d : func.getFormal()) {
            if (d.isUnused()) {
                this.reportError(d, "Unbenutzer Parameter: " + d.getName());
            }
        }
        
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void afterListLiteral(ListLiteral list) throws ASTTraversalException {
        if (list.getContent().isEmpty()) {
            this.reportError(list, "Listen müssen mind. 1 Element enthalten.");
        }
        for (final Expression exp : list.getContent()) {
            for (final Type t : exp.getTypes()) {
                list.addType(new ListType(t), this.unifier);
            }
        }
    }
    
    
    
    @Override
    public void afterProductLiteral(final ProductLiteral product) 
            throws ASTTraversalException {
        
        // Use combinator to create all combinations of possible types
        final CombinationCallBack<Expression, Type> ccb = 
            new CombinationCallBack<Expression, Type>() {

            @Override
            public List<Type> getSubList(Expression outer) {
                return outer.getTypes();
            }

            
            @Override
            public void onNewCombination(List<Type> combination) {
                product.addType(new ProductType(combination), unifier);
            }
        };
        
        Combinator.combine(product.getContent(), ccb);
    }
    
    
    
    @Override
    public void afterAssignment(Assignment assign) throws ASTTraversalException {
        for (final Type t : assign.getExpression().getTypes()) {
            final Declaration vd = new Declaration(assign.getName().getPosition(), 
                assign.getName(), new Empty(t, assign.getExpression().getPosition()));
            this.nspace.declare(vd, this.unifier);
            
            assign.addType(t, this.unifier);
        }
    }
    
    
    
    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.visitCall(call);
    }
    

    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeCall(call);
        
        // resolve parameter types
        call.getRhs().visit(this);
        
        final List<Type> possibleTypes = new ArrayList<Type>(
            call.getRhs().getTypes().size());
        for (final Type rhsType : call.getRhs().getTypes()) {
            final MapType possibleLhs = new MapType(
                (ProductType) rhsType, Type.newTypeVar());
            
            possibleTypes.add(possibleLhs);
        }
        
        // resolve called function's types
        call.getLhs().visit(this);
        
        // sort out all lhs types that do not match the rhs types
        for (final Type possibleLhs : possibleTypes) {
            for (final Type lhs : call.getLhs().getTypes()) {
                final TypeUnifier tmp = new TypeUnifier(this.unifier);
                
                if (tmp.unify(lhs, possibleLhs)) {
                    final MapType mtc = (MapType) 
                        tmp.substitute(lhs);
                    call.addType(tmp.substitute(mtc.getTarget()), tmp);
                }
            }
        }
        
        
        if (call.getTypes().isEmpty()) {
            this.reportError(call.getRhs(),
                "Keine passende Deklaration für den Aufruf von " + 
                Unparser.toString(call.getLhs()) + " gefunden");
        }
        this.afterCall(call);
    }
    
    
    
    @Override
    public void afterCall(Call call) throws ASTTraversalException {
        
        /*final List<Type> newLhsTypes = new ArrayList<Type>();
        final List<Type> newRhsTypes = new ArrayList<Type>();
        
        for (final Type rhsType : call.getRhs().getTypes()) {
            final ProductTypeConstructor s = (ProductTypeConstructor) rhsType;
            
            for (final Type lhsType : call.getLhs().getTypes()) {
                final ProductTypeConstructor newRhs = 
                    (ProductTypeConstructor) this.unifier.fresh(s);
                final MapTypeConstructor possibleLhs = 
                    new MapTypeConstructor(newRhs, 
                    Type.newTypeVar());
                
                if (this.unifier.canUnify(possibleLhs, lhsType)) {
                    final MapTypeConstructor type = (MapTypeConstructor) 
                        this.unifier.substitute(possibleLhs);
                    
                    call.addType(type.getTarget(), this.unifier);
                    newLhsTypes.add(type);
                    newRhsTypes.add(type.getSource());
                }
            }
        }
        
        call.getLhs().setTypes(newLhsTypes, this.unifier);
        call.getRhs().setTypes(newRhsTypes, this.unifier);
        
        if (call.getTypes().isEmpty()) {
            this.reportError(call.getRhs(),
                "Keine passende Deklaration für den Aufruf von " + 
                Unparser.toString(call.getLhs()) + " gefunden");
        }*/
    }
    
    
    
    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {
        final Set<Type> types = this.nspace.lookupFresh(access, this.unifier);
        access.addTypes(types, this.unifier);
    }
    
    
    
    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeAccess(access);
        
        if (!(access.getLhs() instanceof VarAccess)) {
            this.reportError(access.getLhs(), "Operand muss ein Bezeichner sein");
        }
        
        final VarAccess va = (VarAccess) access.getLhs();
        final Namespace last = this.nspace;
        this.nspace = Namespace.forName(va.getIdentifier()).derive(this.nspace);
        access.getRhs().visit(this);
        this.nspace = last;

        access.addTypes(access.getRhs().getTypes(), this.unifier);
        
        this.afterAccess(access);
    }
}
