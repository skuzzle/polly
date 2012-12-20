package de.skuzzle.polly.parsing.ast.lang.operators;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.TernaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;



/**
 * This implements a ternary conditional operator that behaves like the ?: in
 * Java.
 * 
 * @author Simon Taddiken
 */
public class Conditional extends TernaryOperator<BooleanLiteral, Literal, Literal>{

    private static final long serialVersionUID = 1L;

    public Conditional(OpType id) {
        super(id, Type.newTypeVar("A"), Type.BOOLEAN, Type.newTypeVar("A"), 
            Type.newTypeVar("A"));
        
        this.setMustCopy(true);
    }
    
    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, BooleanLiteral first,
            Literal second, Literal third, Position resultPos, Visitor execVisitor) 
                throws ASTTraversalException {
        
        if (first.getValue()) {
            stack.push(second);
        } else {
            stack.push(third);
        }
    }
}
