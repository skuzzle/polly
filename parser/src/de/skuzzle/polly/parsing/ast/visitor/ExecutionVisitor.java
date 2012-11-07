package de.skuzzle.polly.parsing.ast.visitor;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.AssignmentExpression;
import de.skuzzle.polly.parsing.ast.expressions.HardcodedExpression;
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
    public void visitHardCoded(HardcodedExpression hc) throws ASTTraversalException {
        this.beforeHardCoded(hc);
        hc.execute(this.stack, this.nspace, this);
        this.afterHardCoded(hc);
    }
    
    
    
    @Override
    public void visitAssignment(AssignmentExpression assign) 
            throws ASTTraversalException {
        this.beforeAssignment(assign);
        
        if (assign.getDeclaration() instanceof FunctionDeclaration) {
            final FunctionDeclaration funDecl = 
                    (FunctionDeclaration) assign.getDeclaration();
            this.nspace.declareFunctionOverride(funDecl);
            
            // result is a FunctionLiteral for the declared function
            final FunctionLiteral result = new FunctionLiteral(
                assign.getExpression().getPosition(), funDecl);
            result.visit(this);
        } else {
            final VarDeclaration varDecl = (VarDeclaration) assign.getDeclaration();
            this.nspace.declareVarOverride(varDecl);
            varDecl.getExpression().visit(this);
        }
        
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
        
        // first, reresolve declaration
        final FunctionDeclaration funDecl = this.nspace.tryResolveFunction(
            call.getIdentifier(), call.getIdentifier().getDeclaration().getType());
        
        if (funDecl.getExpression() instanceof HardcodedExpression) {
            // call of hardcoded expression. those need the parameters on the stack.
            for (final Expression actual : call.getParameters()) {
                actual.visit(this);
            }
            // now execute
            funDecl.getExpression().visit(this);
        } else {
            // normal function call
            
            // create local namespace and declare all actual parameters in it
            this.enter();
            for(int i = 0; i < call.getParameters().size(); ++i) {
                final Expression actual = call.getParameters().get(i);
                final Parameter formal = funDecl.getFormalParameters().get(i);
                
                final VarDeclaration temp = new VarDeclaration(
                        actual.getPosition(), formal.getName(), actual);
                this.nspace.declareVarOverride(temp);
            }
            
            // execute the expression and leave local namespace afterwards
            funDecl.getExpression().visit(this);
            this.leave();
        }
        
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        this.beforeVarAccess(access);
        
        final VarDeclaration varDecl = this.nspace.tryResolveVar(
            access.getIdentifier());
        
        varDecl.getExpression().visit(this);
        
        this.afterVarAccess(access);
    }
}