package de.skuzzle.polly.parsing.ast.visitor;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.ResolvedParameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Hardcoded;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.expressions.LambdaCall;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;


public class ExecutionVisitor extends DepthFirstVisitor {
    
    private final static AtomicInteger lambdaIds = new AtomicInteger();
    
    /**
     * Creates a unique identifier used for Lambda-FunctionDeclarations.
     * 
     * @param pos Source location of the {@link FunctionLiteral}.
     * @return Unique identifier.
     */
    public final static Identifier getLambdaId(Position pos) {
        return new Identifier(pos, "$lmbd_" + lambdaIds.getAndIncrement());
    }
    
    

    private final LinkedList<Literal> stack;
    private Namespace nspace;
    
    
    public ExecutionVisitor(String executor) {
        this.stack = new LinkedList<Literal>();
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
    public void beforeLiteral(Literal literal) throws ASTTraversalException {
        this.beforeLiteral(literal);
        this.stack.push(literal);
        this.afterLiteral(literal);
    }
    
    
    
    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        this.beforeAccess(access);
        
        // store current ns and switch to new one
        final Namespace backup = this.nspace;
        this.nspace = Namespace.forName(access.getName());
        
        // execute expression and restore old namespace
        access.getRhs().visit(this);
        this.nspace = backup;
        
        this.afterAccess(access);
    }
    
    
    
    @Override
    public void visitHardCoded(Hardcoded hc) throws ASTTraversalException {
        this.beforeHardCoded(hc);
        hc.execute(this.stack, this.nspace, this);
        this.afterHardCoded(hc);
    }
    
    
    
    @Override
    public void visitAssignment(Assignment assign) 
            throws ASTTraversalException {
        this.beforeAssignment(assign);
        
        // result of assignment is the result of the assigned expression
        assign.getExpression().visit(this);
        
        this.afterAssignment(assign);
    }
    
    
    
    @Override
    public void visitLambdaCall(LambdaCall call) throws ASTTraversalException {
        this.beforeLambdaCall(call);
        
        // this will put the FunctionLiteral onto the stack
        call.getLambda().visit(this);
        
        final FunctionLiteral func = (FunctionLiteral) this.stack.pop();
        
        // declare the lambda function in a temporary namespace, then call it like a
        // normal function and return to previous namespace
        this.enter();
        
        this.nspace.declareFunction(func.getFunction());
        final Call lambdaCall = new Call(call.getPosition(), 
                func.getFunction().getName(), call.getParameters());
        lambdaCall.visit(this);
        this.leave();
        
        this.afterLambdaCall(call);
    }
    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        this.beforeCall(call);
        
        final VarDeclaration vd = 
            (VarDeclaration) call.getIdentifier().getDeclaration();
        
        this.enter();
        for (final ResolvedParameter p : call.getResolvedParameters()) {
            final VarDeclaration local = 
                new VarDeclaration(p.getPosition(), p.getName(), p.getActual());
            this.nspace.declare(local);
        }
        vd.getExpression().visit(this);
        this.leave();
        
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        this.beforeVarAccess(access);
        
        final VarDeclaration vd = 
                (VarDeclaration) access.getIdentifier().getDeclaration();
        vd.getExpression().visit(this);
        
        this.afterVarAccess(access);
    }
}