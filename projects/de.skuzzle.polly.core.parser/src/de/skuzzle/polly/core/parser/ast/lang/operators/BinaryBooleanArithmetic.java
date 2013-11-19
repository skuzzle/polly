package de.skuzzle.polly.core.parser.ast.lang.operators;

import java.util.Random;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class BinaryBooleanArithmetic extends BinaryOperator<BooleanLiteral, BooleanLiteral> {
    
    private final static Random RANDOM = new Random();

    public BinaryBooleanArithmetic(OpType id) {
        super(id);
        this.initTypes(Type.BOOLEAN, Type.BOOLEAN, Type.BOOLEAN);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, BooleanLiteral left,
            BooleanLiteral right, Position resultPos, ExecutionVisitor execVisitor)
                throws ASTTraversalException {
        
        switch (this.getOp()) {
        case BOOLEAN_AND:
            stack.push(
                new BooleanLiteral(resultPos, left.getValue() && right.getValue()));
            break;
        case BOOLEAN_OR:
            stack.push(
                new BooleanLiteral(resultPos, left.getValue() || right.getValue()));
            break;
        case XOR:
            stack.push(
                new BooleanLiteral(resultPos, left.getValue() ^ right.getValue()));
            break;
        
        case IMPLICATION:
            stack.push(new BooleanLiteral(resultPos, 
                    !(left.getValue() && !right.getValue())));
            break;
            
        case EQUIVALENCE:
            stack.push(new BooleanLiteral(resultPos, 
                    left.getValue() == right.getValue()));
            
            break;
            
        case AND_OR:
            BooleanLiteral result = null;
            if (RANDOM.nextFloat() > 0.8) {
                result = new BooleanLiteral(resultPos, RANDOM.nextBoolean());
            } else {
                final float r = RANDOM.nextFloat();
                if (r < 0.33f) {
                    result = new BooleanLiteral(resultPos, left.getValue());
                } else if (r < 0.66f) {
                    result = new BooleanLiteral(resultPos, right.getValue());
                } else {
                    result = new BooleanLiteral(resultPos, 
                            left.getValue() && right.getValue());
                }
            }
            stack.push(result);
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
