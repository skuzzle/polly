package de.skuzzle.polly.parsing.ast.lang.operators;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.lang.UnaryOperator;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;


public class UnaryArithmetic extends UnaryOperator<NumberLiteral> {

    private static final long serialVersionUID = 1L;
    
    public UnaryArithmetic(OpType op) {
        super(op, Type.NUMBER, Type.NUMBER);
    }

    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, NumberLiteral operand,
            Position resultPos) {
        
        switch (this.getOp()) {
        case SUB:
            stack.push(new NumberLiteral(resultPos, -operand.getValue()));
            break;
        case LOG:
            stack.push(new NumberLiteral(resultPos, Math.log10(operand.getValue())));
            break;
        case LN:
            stack.push(new NumberLiteral(resultPos, Math.log(operand.getValue())));
            break;
        case SQRT:
            stack.push(new NumberLiteral(resultPos, Math.sqrt(operand.getValue())));
            break;
        case CEIL:
            stack.push(new NumberLiteral(resultPos, Math.ceil(operand.getValue())));
            break;
        case FLOOR:
            stack.push(new NumberLiteral(resultPos, Math.floor(operand.getValue())));
            break;
        case ROUND:
            stack.push(new NumberLiteral(resultPos, Math.round(operand.getValue())));
            break;
        case SIG:
            stack.push(new NumberLiteral(resultPos, Math.signum(operand.getValue())));
            break;
        case COS:
            stack.push(new NumberLiteral(resultPos, Math.cos(operand.getValue())));
            break;
        case SIN:
            stack.push(new NumberLiteral(resultPos, Math.sin(operand.getValue())));
            break;
        case TAN:
            stack.push(new NumberLiteral(resultPos, Math.tan(operand.getValue())));
            break;
        case ASIN:
            stack.push(new NumberLiteral(resultPos, Math.asin(operand.getValue())));
            break;
        case ACOS:
            stack.push(new NumberLiteral(resultPos, Math.acos(operand.getValue())));
            break;
        case ATAN:
            stack.push(new NumberLiteral(resultPos, Math.atan(operand.getValue())));
            break;
        case ABS:
            stack.push(new NumberLiteral(resultPos, Math.abs(operand.getValue())));
            break;
        case TO_DEGREES:
            stack.push(new NumberLiteral(resultPos, Math.toDegrees(operand.getValue())));
            break;
        case TO_RADIANS:
            stack.push(new NumberLiteral(resultPos, Math.toRadians(operand.getValue())));
            break;
        case EXP:
            stack.push(new NumberLiteral(resultPos, Math.exp(operand.getValue())));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
