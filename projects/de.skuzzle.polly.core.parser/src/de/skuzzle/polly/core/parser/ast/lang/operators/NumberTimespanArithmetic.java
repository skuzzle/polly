package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;

public class NumberTimespanArithmetic extends
        BinaryOperator<NumberLiteral, TimespanLiteral> {

    public NumberTimespanArithmetic() {
        super(OpType.MUL);
        this.initTypes(Type.TIMESPAN, Type.NUM, Type.TIMESPAN);
    }



    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, NumberLiteral left,
            TimespanLiteral right, Position resultPos, ExecutionVisitor execVisitor)
            throws ASTTraversalException {

        switch (this.getOp()) {
        case MUL:
            final int factor = left.isInteger(left.getPosition(), 
                    execVisitor.getReporter());
            final TimespanLiteral result = new TimespanLiteral(resultPos, 
                    right.getSeconds() * factor);
            stack.push(result);
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
