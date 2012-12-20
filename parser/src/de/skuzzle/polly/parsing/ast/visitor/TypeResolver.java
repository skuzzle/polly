package de.skuzzle.polly.parsing.ast.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.TypeDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Typespace;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Empty;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.parameters.FunctionParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.ListParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.util.LinkedStack;
import de.skuzzle.polly.parsing.util.Stack;



public class TypeResolver extends DepthFirstVisitor {
    
    
    
    private Namespace nspace;
    private final Namespace rootNs;
    private final Set<Node> checked;
    private Stack<Call> signatureStack;
    private final Typespace types;
    
    
    
    public TypeResolver(Namespace namespace) {
        // create temporary namespace for executing user
        this.rootNs = namespace.enter(false);
        this.nspace = this.rootNs;
        this.checked = new HashSet<Node>();
        this.signatureStack = new LinkedStack<Call>();
        this.types = new Typespace();
    }
    
    
    
    /**
     * Creates a new sub namespace of the current namespace and sets that new namespace
     * as the current one.
     * 
     * @return The created namespace.
     */
    private Namespace enter() {
        return this.nspace = this.nspace.enter(true);
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
        // TODO: remove, or what?
        this.checked.add(node);
        return false;
    }
    
    
    
    @Override
    public void visitNative(Native hc) throws ASTTraversalException {
        this.beforeNative(hc);
        hc.resolveType(this.nspace, this);
        this.afterNative(hc);
    }
    
    
    
    @Override
    public void visitParameter(Parameter param) throws ASTTraversalException {
        this.beforeParameter(param);
        final TypeDeclaration decl = this.types.resolveType(param.getName());
        param.setUnique(decl.getType());
        
        this.afterParameter(param);
    }
    
    
    
    @Override
    public void visitListParameter(ListParameter param) throws ASTTraversalException {
        this.beforeListParameter(param);
        
        if (param.getTypeName() != null) {
            final TypeDeclaration subTypeDecl = 
                this.types.resolveType(param.getTypeName());
            param.setUnique(new ListTypeConstructor(subTypeDecl.getType()));
        } else {
            param.setUnique(new ListTypeConstructor(TypeVar.create()));
        }
        
        this.afterListParameter(param);
    }
    
    
    
    @Override
    public void visitFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        this.beforeFunctionParameter(param);
        
        final Iterator<ResolvableIdentifier> it = param.getSignature().iterator();
        
        // first element is the return type
        final TypeDeclaration returnTypeDecl = this.types.resolveType(it.next());
        final List<Type> types = new ArrayList<Type>(param.getSignature().size());
        while (it.hasNext()) {
            final TypeDeclaration decl = this.types.resolveType(it.next());
            types.add(decl.getType());
        }
        
        param.setUnique(new MapTypeConstructor(types, returnTypeDecl.getType()));
        
        this.afterFunctionParameter(param);
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
        
        // If this is the lhs of a function call, the called signature will be on top
        // of the signature stack. So we are able to use the actual called signature
        final Call c = this.signatureStack.isEmpty() 
                ? null 
                : this.signatureStack.pop();
        
        this.enter();
        final Iterator<Parameter> formalIt = func.getFormal().iterator();
        final Iterator<Expression> actualIt = c == null ? null :
            c.actualParameters.iterator();
                
        while (formalIt.hasNext()) {
            final Parameter p = formalIt.next();
            // resolve parameter type
            p.visit(this);
            
            // use both the type of the actual signature, and the type of the formal 
            // signature, depending on what information we have. The actual signature
            // might have more concrete type information, but may as well be totally
            // wrong. If it was wrong, we can fallback to the actual signature
            
            // HACK or not? declaring both, the formal and actual signature here. The 
            //     second is a fallback if the actual signature did not match
            if (actualIt != null) {
                final Expression actual = actualIt.next();
                final Position pos = actual.getPosition();
                
                final VarDeclaration vd = new VarDeclaration(
                    pos, p.getName(), 
                    new Empty(actual.getUnique(), pos, this.signatureStack));
                
                this.nspace.declare(vd);
            }
            
            
            final VarDeclaration vd = new VarDeclaration(p.getPosition(), p.getName(), 
                new Empty(p.getUnique(), p.getPosition(), this.signatureStack));
            
            this.nspace.declare(vd);
        }
        // now determine type of the function's expression
        func.getExpression().visit(this);
        this.leave();
        
        final FunctionType resultType = c == null 
            ? new FunctionType(func.getExpression().getUnique(), 
                    Parameter.asType(func.getFormal())) 
            : new FunctionType(func.getExpression().getUnique(), 
                    c.signature.getParameters());
            
        
        func.setUnique(resultType);
        func.setReturnType(resultType.getReturnType());
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        if (this.testIsChecked(list)) {
            return;
        }
        
        this.beforeListLiteral(list);
        
        if (list.getContent().isEmpty()) {
            throw new ASTTraversalException(list.getPosition(), "Leere Liste");
        } else {
            // resolve expression types
            Type last = null; 
            for (final Expression exp : list.getContent()) {
                exp.visit(this);
                if (last != null && !last.equals(exp.getUnique())) {
                    throw new ASTTraversalException(exp.getPosition(), 
                        "Listen dürfen nur Elemente des selben Typs beinhalten");
                }
                last = exp.getUnique();
            }
            list.getUnique().isUnifiableWith(last);
        }
        
        this.afterListLiteral(list);
    }
    
    
    
    @Override
    public void visitAssignment(final Assignment assign) throws ASTTraversalException {
        if (this.testIsChecked(assign)) {
            return;
        }
        
        this.beforeAssignment(assign);
        // resolve assignments type
        assign.getExpression().visit(this);
        assign.setUnique(assign.getExpression().getUnique());
        
        /*final Empty exp = new Empty(
                assign.getExpression().getType(), assign.getExpression().getPosition(), 
                this.signatureStack); */
        
        final VarDeclaration vd = new VarDeclaration(assign.getPosition(), 
            assign.getName(), assign.getExpression());
        
        // exchange assignment with its sole expression
        // this needs to be done in case that further assignments are following. They 
        // would then contain this assignment too. 
        //assign.getParent().replaceChild(assign, assign.getExpression());
        
        // Transitive deep recursion check: check whether assigned expression contains
        //                                  transitive relations to itself.
        if (vd.getType() instanceof FunctionType) {
            final Visitor recursiveCallChecker = new DepthFirstVisitor() {
                
                @Override
                public void beforeVarAccess(VarAccess access) 
                        throws ASTTraversalException {
                    
                    final VarDeclaration test = (VarDeclaration) nspace.tryResolve(
                            access.getIdentifier(), vd.getType());
                    
                    if (test != null) {
                        if (vd.equals(test)) {
                            throw new ASTTraversalException(vd.getPosition(), 
                                "Rekursive Deklaration von '" + access.getIdentifier() + 
                                "'");
                        }
                        test.getExpression().visit(this);
                    }
                }
            };
            assign.getExpression().visit(recursiveCallChecker);
        }
        
        // declarations are always stored in the root namespace!
        this.rootNs.declare(vd);
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
        final ProductTypeConstructor signature = call.createSignature();
        
        // push call signature.
        this.signatureStack.push(call);
        
        // check what type of call this is. Might be a lambda call or a VarAccess which
        // in turn references a function. 
        call.getLhs().visit(this);
        
        // Choose a matching type from the possible types of the LHS
        if (call.getLhs().typeResolved()) {
            if (!(call.getLhs().getUnique() instanceof MapTypeConstructor)) {
                throw new ASTTraversalException(call.getLhs().getPosition(), 
                    "Funktion erwartet");
            }
            final MapTypeConstructor mc = (MapTypeConstructor) call.getLhs().getUnique();
            call.setUnique(mc.getTarget());
        } else {
            for (final Type t : call.getLhs().getTypes()) {
                // only interested in functions here
                if (!(t instanceof MapTypeConstructor)) {
                    continue;
                }
                
                final MapTypeConstructor mc = (MapTypeConstructor) t;
                final ProductTypeConstructor pc = new ProductTypeConstructor(mc.getSource());
                if (pc.isUnifiableWith(signature)) {
                    call.getLhs().setUnique(mc);
                    call.setUnique(mc.getTarget());
                    break;
                } else {
                    call.addType(mc.getTarget());
                }
            }
        }
        
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        if (access.getIdentifier().getDeclaration() != null) {
            // do not double check (accessed expression is already resolved
            return;
        }
        
        this.beforeVarAccess(access);
        
        access.addTypes(this.nspace.lookup(access.getIdentifier()));
        
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
        final Expression lhs = access.getLhs();
        if (!(lhs instanceof VarAccess)) {
            throw new ASTTraversalException(access.getPosition(), 
                "Operanden müssen Bezeichner sein.");
        }
        final VarAccess va = (VarAccess) lhs;
        this.nspace = Namespace.forName(va.getIdentifier()).derive(this.nspace);
        access.getRhs().visit(this);
        this.nspace = backup;
        
        access.setUnique(access.getRhs().getUnique());
        
        this.afterAccess(access);
    }
}