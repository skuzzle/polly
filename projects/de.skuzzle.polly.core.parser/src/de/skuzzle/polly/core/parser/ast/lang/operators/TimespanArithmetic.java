package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.tools.collections.Stack;

/**
 * Binary operators that operate on {@link TimespanLiteral}s and produce a new 
 * {@link TimespanLiteral}.
 * 
 * @author Simon Taddiken
 */
public class TimespanArithmetic extends BinaryOperator<TimespanLiteral, TimespanLiteral>{

    public TimespanArithmetic(OpType id) {
        super(id);
        this.initTypes(Type.TIMESPAN, Type.TIMESPAN, Type.TIMESPAN);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, TimespanLiteral left,
            TimespanLiteral right, Position resultPos, ASTVisitor execVisitor) 
                throws ASTTraversalException {
        
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
