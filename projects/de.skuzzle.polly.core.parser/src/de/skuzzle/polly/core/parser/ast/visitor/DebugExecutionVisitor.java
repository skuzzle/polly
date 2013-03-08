package de.skuzzle.polly.core.parser.ast.visitor;

import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;


public class DebugExecutionVisitor extends ExecutionVisitor {

    private final static int INDENT_STEP = 4;
    
    private int indent;
    
    
    public DebugExecutionVisitor(Namespace rootNs, Namespace workingNs) {
        super(rootNs, workingNs);
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
    public int before(Call call) throws ASTTraversalException {
        this.println("Call: " + Unparser.toString(call.getLhs()));
        this.indent();
        this.println("Unique: " + call.getUnique());
        this.println("Position: " + call.getPosition());
        this.indent();
        return CONTINUE;
    }
    
    
    
    @Override
    public int after(Call call) throws ASTTraversalException {
        this.println("Result: " + this.stack.peek());
        this.unindent();
        this.unindent();
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(ListLiteral list) throws ASTTraversalException {
        this.println("List:" + list.toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Literal literal) throws ASTTraversalException {
        this.println("Literal: " + literal.toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(FunctionLiteral func) throws ASTTraversalException {
        this.println("Function: " + func.toString());
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(VarAccess access) throws ASTTraversalException {
        this.println("Resolving: " + access.getIdentifier() + 
            ", type: " + access.getUnique());
        return CONTINUE;
    }
}
