package de.skuzzle.polly.parsing.ast.operators.unary;

import java.util.LinkedList;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.operators.UnaryOperator;
import de.skuzzle.polly.parsing.types.Type;


public class Arithmetic extends UnaryOperator<NumberLiteral> {

    public Arithmetic(OpType op) {
        super(op, Type.NUMBER, Type.NUMBER);
    }

    
    @Override
    protected void exec(LinkedList<Literal> stack, Namespace ns, NumberLiteral operand,
            Position resultPos) {
        
        if (this.getOp() == OpType.SUB) {
            stack.push(new NumberLiteral(resultPos, -operand.getValue()));
        }
    }
}
