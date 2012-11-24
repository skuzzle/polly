package de.skuzzle.polly.parsing.ast.operators;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Stack;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.Type;

/**
 * Binary operators that operate on {@link TimespanLiteral}s and produce a new 
 * {@link TimespanLiteral}.
 * 
 * @author Simon Taddiken
 */
public class TimespanArithmetic extends BinaryOperator<TimespanLiteral, TimespanLiteral>{

    public TimespanArithmetic(OpType id) {
        super(id, Type.TIMESPAN, Type.TIMESPAN, Type.TIMESPAN);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, TimespanLiteral left,
            TimespanLiteral right, Position resultPos) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case ADD:
            stack.push(new TimespanLiteral(resultPos, 
                left.getSeconds() + right.getSeconds()));
            break;
        case SUB:
            stack.push(new TimespanLiteral(resultPos, 
                left.getSeconds() - right.getSeconds()));
            break;
        default:
                this.invalidOperatorType(this.getOp());
        }
    }
    

}
