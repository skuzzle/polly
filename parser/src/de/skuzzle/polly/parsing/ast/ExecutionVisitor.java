package de.skuzzle.polly.parsing.ast;

import java.util.LinkedList;

import de.skuzzle.polly.parsing.ast.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.VarOrCall;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;


public class ExecutionVisitor extends DepthFirstVisitor {

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
        this.stack.push(literal);
    }
    
    
    
    @Override
    public void visitVarOrCall(VarOrCall call) throws ASTTraversalException {
        if (call.isCall()) {
            // first, reresolve declaration
            final FunctionDeclaration funDecl = this.nspace.tryResolveFunction(
                call.getIdentifier(), call.getIdentifier().getDeclaration().getType());
            
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
            
        } else if (call.isVarAccess()) {
            // reresolve variable and execute the resolved expression
            final VarDeclaration varDecl = this.nspace.tryResolveVar(
                call.getIdentifier());
            
            varDecl.getExpression().visit(this);
        }
    }
}