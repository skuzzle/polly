package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;


public class DebugExecutionVisitor extends ExecutionVisitor {

    private final static int INDENT_STEP = 4;
    
    private int indent;
    
    
    public DebugExecutionVisitor(Namespace namespace) {
        super(namespace);
    }
    
    
    
    private void indent() {
        ++this.indent;
    }
    
    
    
    private void unindent() {
        --this.indent;
    }
    
    
    
    private void print(String s) {
        for (int i = 0; i < indent * INDENT_STEP; ++i) {
            System.out.print(" ");
        }
        System.out.print(s);
    }
    
    
    
    private void println(String s) {
        this.print(s);
        System.out.println();
    }
    

    
    
    @Override
    public void beforeCall(Call call) throws ASTTraversalException {
        this.println("Call: " + Unparser.toString(call.getLhs()));
        this.indent();
        this.println("Unique: " + call.getUnique());
        this.println("Position: " + call.getPosition());
        this.indent();
    }
    
    
    
    @Override
    public void afterCall(Call call) throws ASTTraversalException {
        this.println("Result: " + this.stack.peek());
        this.unindent();
        this.unindent();
    }
    
    
    
    @Override
    public void beforeListLiteral(ListLiteral list) throws ASTTraversalException {
        this.println("List:" + list.toString());
    }
    
    
    
    @Override
    public void beforeLiteral(Literal literal) throws ASTTraversalException {
        this.println("Literal: " + literal.toString());
    }
    
    
    
    @Override
    public void beforeFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        this.println("Function: " + func.toString());
    }
    
    
    
    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {
        this.println("Resolving: " + access.getIdentifier() + 
            ", type: " + access.getUnique());
    }
}
