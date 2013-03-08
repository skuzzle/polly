package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;
import de.skuzzle.polly.core.parser.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.lang.TernaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.tools.collections.Stack;



/**
 * This implements a ternary conditional operator that behaves like the ?: in
 * Java.
 * 
 * @author Simon Taddiken
 */
public class Conditional extends TernaryOperator<BooleanLiteral, Literal, Literal>{

    public Conditional(OpType id) {
        super(id);
        final TypeVar a = Type.newTypeVar("A");
        this.initTypes(a, Type.BOOLEAN, a, a);
    }
    
    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, BooleanLiteral first,
            Literal second, Literal third, Position resultPos, ASTVisitor execVisitor) 
                throws ASTTraversalException {
        
        if (first.getValue()) {
            stack.push(second);
        } else {
            stack.push(third);
        }
    }
}
