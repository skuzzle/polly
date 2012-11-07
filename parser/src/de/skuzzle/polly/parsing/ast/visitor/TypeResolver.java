package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.LambdaCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.types.FunctionType;


public class TypeResolver extends DepthFirstVisitor {

    private Namespace nspace;
    
    
    public TypeResolver(String executor) {
        this.nspace = Namespace.forName(executor);
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
    
    
    
    
    @Override
    public void visitLambdaCall(LambdaCall call) throws ASTTraversalException {
        this.beforeLambdaCall(call);
        
        this.enter();
        // TODO goon
        // first, resolve actual parameter types 
        for (final Expression actual : call.getParameters()) {
            actual.visit(this);
        }
        
        // dertermine 
        call.getLambda().getFunction().visit(this);
        
        this.afterLambdaCall(call);
    }
    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        this.beforeCall(call);
        
        // first, resolve actual parameter types 
        for (final Expression actual : call.getParameters()) {
            actual.visit(this);
        }
        
        final FunctionType signature = call.createSignature();
        final FunctionDeclaration funcDecl = this.nspace.resolveFunction(
            call.getIdentifier(), signature);
        
        // type of a call statement is always the type of the called expression
        call.setType(funcDecl.getExpression().getType());
        
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        this.beforeVarAccess(access);
        
        final VarDeclaration vd = this.nspace.resolveVar(access.getIdentifier());
        access.setType(vd.getExpression().getType());
        
        this.afterVarAccess(access);
    }
}