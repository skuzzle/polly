package de.skuzzle.polly.parsing.ast.visitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.ResolvedParameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Hardcoded;
import de.skuzzle.polly.parsing.ast.expressions.LambdaCall;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.operators.Operator;
import de.skuzzle.polly.parsing.ast.operators.Operator.OpType;
import de.skuzzle.polly.parsing.ast.operators.binary.Arithmetic;



public class ExecutionVisitor extends DepthFirstVisitor {
    
    public static void main(String[] args) throws ASTTraversalException, IOException {
        final Operator add = new Arithmetic(OpType.ADD);
        final Operator sub = new Arithmetic(OpType.SUB);
        final Operator mul = new Arithmetic(OpType.MUL);
        final Operator div = new Arithmetic(OpType.DIV);
        Namespace.forName("me").declare(add.createDeclaration());
        Namespace.forName("me").declare(sub.createDeclaration());
        Namespace.forName("me").declare(mul.createDeclaration());
        Namespace.forName("me").declare(div.createDeclaration());
        
        Expression left = new NumberLiteral(Position.EMPTY, 5.0);
        Expression right = new NumberLiteral(Position.EMPTY, 2.0);
        OperatorCall binary1 = OperatorCall.binary(Position.EMPTY, OpType.ADD, left, right);
     
        left = binary1;
        right = new NumberLiteral(Position.EMPTY, 8.0);
        OperatorCall binary2 = OperatorCall.binary(Position.EMPTY, OpType.MUL, left, right);
        
        TypeResolver tr = new TypeResolver("me");
        binary2.visit(tr);
        
        ExecutionVisitor ev = new ExecutionVisitor("me");
        binary2.visit(ev);
        
        System.out.println("Result: " + ev.stack.peek());
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
    public void visitLiteral(Literal literal) throws ASTTraversalException {
        this.beforeLiteral(literal);
        this.stack.push(literal);
        this.afterLiteral(literal);
    }
    
    
    
    @Override
    public void visitFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        this.beforeFunctionLiteral(func);
        this.stack.push(func);
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        // create collection of executed list content
        final Collection<Expression> executed = new ArrayList<Expression>();
        for (final Expression exp : list.getContent()) {
            // places executed expression on the stack
            exp.visit(this);
            executed.add(this.stack.pop());
        }
        final ListLiteral result = new ListLiteral(list.getPosition(), executed);
        this.stack.push(result);
    }
    
    
    
    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        this.beforeAccess(access);
        
        // store current ns and switch to new one
        final Namespace backup = this.nspace;
        
        // get namespace which is accessed here and has the current namespace as 
        // parent. 
        // lhs of access is guaranteed to be a VarAccess
        final VarAccess va = (VarAccess) access.getLhs();
        this.nspace = Namespace.forName(va.getIdentifier()).derive(this.nspace);
        
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
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeOperatorCall(call);
        this.visitCall(call);
        this.afterOperatorCall(call);
    }
    
    
    
    @Override
    public void visitLambdaCall(LambdaCall call) throws ASTTraversalException {
        this.beforeLambdaCall(call);
        
        // this will put the FunctionLiteral onto the stack
        call.getLambda().visit(this);
        
        final FunctionLiteral func = (FunctionLiteral) this.stack.pop();
        
        // create fake declaration
        final Declaration vd = new VarDeclaration(func.getPosition(), 
                call.getIdentifier(), func);
        
        final ResolvableIdentifier fakeId = new ResolvableIdentifier(vd.getName());
        fakeId.setDeclaration(vd);
        
        final Call lambdaCall = new Call(call.getPosition(), 
                fakeId, call.getParameters());
        lambdaCall.setResolvedParameters(call.getResolvedParameters());
        lambdaCall.visit(this);
        
        this.afterLambdaCall(call);
    }
    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        this.beforeCall(call);
        
        final VarDeclaration vd = 
            (VarDeclaration) call.getIdentifier().getDeclaration();
        
        this.enter();
        for (final ResolvedParameter p : call.getResolvedParameters()) {
            // execute actual parameter
            p.getActual().visit(this);
            
            // declare result as local variable for this call
            final Expression actual = this.stack.pop();
            
            final VarDeclaration local = 
                new VarDeclaration(p.getPosition(), p.getName(), actual);
            this.nspace.declare(local);
        }

        final FunctionLiteral func = (FunctionLiteral) vd.getExpression();
        func.getExpression().visit(this);
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