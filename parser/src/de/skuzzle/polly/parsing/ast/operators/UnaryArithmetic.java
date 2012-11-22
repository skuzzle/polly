package de.skuzzle.polly.parsing.ast.operators;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Stack;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.types.Type;


public class UnaryArithmetic extends UnaryOperator<NumberLiteral> {

    public UnaryArithmetic(OpType op) {
        super(op, Type.NUMBER, Type.NUMBER);
    }

    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, NumberLiteral operand,
            Position resultPos) {
        
        if (this.getOp() == OpType.SUB) {
            stack.push(new NumberLiteral(resultPos, -operand.getValue()));
        }
    }
}
