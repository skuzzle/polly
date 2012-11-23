package de.skuzzle.polly.parsing.ast.operators;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Stack;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.Type;

/**
 * Contains arithmetic operators that operate on {@link NumberLiteral}s and produce a
 * new NumberLiteral.
 *  
 * @author Simon Taddiken
 */
public class BinaryArithmetic extends BinaryOperator<NumberLiteral, NumberLiteral> {

    
    public BinaryArithmetic(OpType id) {
        super(id, Type.NUMBER, Type.NUMBER, Type.NUMBER);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns,
            NumberLiteral left, NumberLiteral right, 
            Position resultPos) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case ADD: 
            stack.push(new NumberLiteral(resultPos, left.getValue() + right.getValue()));
            break;
        case SUB:
            stack.push(new NumberLiteral(resultPos, left.getValue() - right.getValue()));
            break;
        case MUL:
            stack.push(new NumberLiteral(resultPos, left.getValue() * right.getValue()));
            break;
        case DIV:
            right.nonZero();
            stack.push(new NumberLiteral(resultPos, left.getValue() / right.getValue()));
            break;
        case INTDIV:
            right.nonZero();
            // XXX: implicit conversion
            stack.push(new NumberLiteral(resultPos, 
                Math.ceil(left.getValue()) / Math.ceil(right.getValue())));
            break;
        case MOD:
            int r = right.nonZeroInteger();
            int l = left.isInteger() % r;
            if (l < 0) {
                l += r;
            }
            stack.push(new NumberLiteral(resultPos, l));
        case INT_AND:
            stack.push(new NumberLiteral(resultPos, 
                left.isInteger() & right.isInteger()));
            break;
        case INT_OR:
            stack.push(new NumberLiteral(resultPos, 
                left.isInteger() | right.isInteger()));
            break;
        case LEFT_SHIFT:
            stack.push(new NumberLiteral(resultPos,
                    left.isInteger() << right.isInteger()));
            break;
        case RIGHT_SHIFT:
            stack.push(new NumberLiteral(resultPos,
                left.isInteger() >> right.isInteger()));
            break;
        case URIGHT_SHIFT:
            stack.push(new NumberLiteral(resultPos,
                left.isInteger() >>> right.isInteger()));
            break;
        case RADIX:
            right.setRadix(left.isInteger());
            stack.push(right);
            break;
        case POWER:
            stack.push(new NumberLiteral(resultPos, 
                Math.pow(left.getValue(), right.getValue())));
            break;
        default:
            throw new RuntimeException("This should not have happened. Binary " +
            		"operator call with invalid operator type");
        }
    }
}