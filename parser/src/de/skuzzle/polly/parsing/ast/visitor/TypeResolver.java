package de.skuzzle.polly.parsing.ast.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.ResolvedParameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.LambdaCall;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;


public class TypeResolver extends DepthFirstVisitor {
    
    /**
     * Expression that does nothing except to represent the type that has been set in the
     * Constructor.
     * 
     * @author Simon Taddiken
     */
    private final static class EmptyExpression extends Expression {

        public EmptyExpression(Type type) {
            super(Position.EMPTY, type);
        }
        
        @Override
        public void visit(Visitor visitor) throws ASTTraversalException {}
    }
    
    

    private Namespace nspace;
    private final Namespace rootNs;
    private final Set<Node> checked;
    
    
    
    public TypeResolver(String namespace) {
        // create temporary namespace for executing user
        this.nspace = Namespace.forName(namespace);
        this.rootNs = nspace;
        this.checked = new HashSet<Node>();
    }
    
    
    
    /**
     * Creates a new sub namespace of the current namespace and sets that new namespace
     * as the current one.
     * 
     * @return The created namespace.
     */
    private Namespace enter() {
        return this.nspace = this.nspace.enter("local");
    }
    
    
    
    /**
     * Sets the current namespace as the parent of the current namespace.
     * 
     * @return The parent of the former current namespace.
     */
    private Namespace leave() {
        return this.nspace = this.nspace.getParent();
    }
    
    
    
    /**
     * Tests whether the given node has already been checked. If it was not already 
     * checked, it will be marked checked by the time you call this method.
     * 
     * @param node The node to check.
     * @return Whether the nodes type has already been resolved.
     */
    private final boolean testIsChecked(Node node) {
        return !this.checked.add(node);
    }
    
    
    
    @Override
    public void visitFunctionLiteral(FunctionLiteral func)
            throws ASTTraversalException {
        
        if (this.testIsChecked(func)) {
            return;
        }
        
        this.beforeFunctionLiteral(func);

        // add formal parameters as empty expressions into new local namespace, then
        // context check the functions expression to resolve the return type
        this.enter();
        final Iterator<Parameter> formalIt = func.getFormal().iterator();
        while (formalIt.hasNext()) {
            final Parameter p = formalIt.next();
            final VarDeclaration vd = new VarDeclaration(
                p.getPosition(), p.getName(), new EmptyExpression(p.getType()));
            this.nspace.declare(vd);
        }
        // now determine type of the function's expression
        func.getExpression().visit(this);
        this.leave();
        
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        if (this.testIsChecked(list)) {
            return;
        }
        
        this.beforeListLiteral(list);
        
        if (list.getContent().isEmpty()) {
            list.setType(Type.EMPTY_LIST);
        } else {
            // resolve expression types
            Type last = null; 
            for (final Expression exp : list.getContent()) {
                exp.visit(this);
                if (last != null && !last.check(exp.getType())) {
                    throw new ASTTraversalException(exp.getPosition(), 
                        "Listen dürfen nur Elemente des selben Typs beinhalten");
                }
                last = exp.getType();
            }
            list.setType(new ListType(last));
        }
        
        this.afterListLiteral(list);
    }
    
    
    
    @Override
    public void visitAssignment(Assignment assign) throws ASTTraversalException {
        if (this.testIsChecked(assign)) {
            return;
        }
        
        this.beforeAssignment(assign);
        // resolve assignments type
        assign.getExpression().visit(this);
        assign.setType(assign.getExpression().getType());
        
        // declarations are always stored in the root namespace!
        this.rootNs.declare(assign.getDeclaration());
        
        this.afterAssignment(assign);
    }
    
    
    
    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeOperatorCall(call);
        // treat as normal call
        this.visitCall(call);
        this.afterOperatorCall(call);
    }
    
    
    
    @Override
    public void visitLambdaCall(LambdaCall call) throws ASTTraversalException {
        if (this.testIsChecked(call)) {
            return;
        }
        
        
        // NOTE: all calls (declared function calls and operator calls) are mapped to
        //       lambda calls.
        
        
        this.beforeLambdaCall(call);
        
        final FunctionLiteral func = call.getLambda();
        
        // resolve functions return type
        func.visit(this);
        
        // validate call. If this was a call to a declared function instead of 
        // a lambda function, parameter types will match anyway, as declared functions
        // are resolved by their signature
        if (call.getParameters().size() != func.getFormal().size()) {
            throw new ASTTraversalException(call.getPosition(), 
                "Ungültige Parameteranzahl. Erwartet: " + func.getFormal());
        }
        
        final Iterator<Parameter> formalIt = func.getFormal().iterator();
        final Iterator<Expression> actualIt = call.getParameters().iterator();
        final List<ResolvedParameter> resolved = new ArrayList<ResolvedParameter>();
        
        while (formalIt.hasNext()) {
            final Expression actual = actualIt.next();
            final Parameter formal = formalIt.next();

            // resolve actual parameter's type and check against formal
            actual.visit(this);
            
            if (!actual.getType().check(formal.getType())) {
                Type.typeError(actual.getType(), formal.getType(), actual.getPosition());
            }
            resolved.add(new ResolvedParameter(
                actual.getPosition(), formal.getName(), actual));
        }
        call.setResolvedParameters(resolved);
        // type of the call is the type of the called expression
        call.setType(func.getExpression().getType());
        func.setReturnType(func.getExpression().getType());
        
        this.afterLambdaCall(call);
    }
    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        if (this.testIsChecked(call)) {
            return;
        }
        
        this.beforeCall(call);
        
        // resolve actual parameter types
        for (final Expression exp : call.getParameters()) {
            exp.visit(this);
        }
        
        // create signature from actual parameter types.
        // signature does *not* obey return value as it is unknown by now.
        final Type sig = call.createSignature(); 
        final VarDeclaration decl = this.nspace.resolveVar(call.getIdentifier(), sig);
        
        if (!(decl.getExpression().getType() instanceof FunctionType)) {
            throw new ASTTraversalException(call.getIdentifier().getPosition(), 
                call.getIdentifier().getId() + " ist keine Funktion");
        }

        // now treat resolved function as a lambda function
        final FunctionLiteral func = (FunctionLiteral) decl.getExpression();
        final LambdaCall delegate = new LambdaCall(
            call.getPosition(), func, call.getParameters());
        delegate.visit(this);
        
        // apply resolved type to this call
        call.setType(delegate.getType());
        call.setResolvedParameters(delegate.getResolvedParameters());
        
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        if (this.testIsChecked(access)) {
            return;
        }
        
        this.beforeVarAccess(access);
        
        final VarDeclaration vd = this.nspace.resolveVar(
                access.getIdentifier(), Type.ANY);
        access.setType(vd.getType());
        
        this.afterVarAccess(access);
    }
    
    
    
    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        if (this.testIsChecked(access)) {
            return;
        }
        this.beforeAccess(access);
        // remember current nspace
        final Namespace backup = this.nspace;
        
        // get namespace which is accessed here and has the current namespace as 
        // parent. 
        this.nspace = Namespace.forName(access.getName()).derive(this.nspace);
        access.getRhs().visit(this);
        this.nspace = backup;
        
        access.setType(access.getRhs().getType());
        
        this.afterAccess(access);
    }
}