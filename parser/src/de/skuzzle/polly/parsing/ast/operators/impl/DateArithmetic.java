package de.skuzzle.polly.parsing.ast.operators.impl;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.expressions.literals.DateLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.parsing.ast.operators.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;


public class DateArithmetic extends BinaryOperator<DateLiteral, DateLiteral>{

    public DateArithmetic(OpType id) {
        super(id, Type.TIMESPAN, Type.DATE, Type.DATE);
    }

    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, DateLiteral left,
            DateLiteral right, Position resultPos) throws ASTTraversalException {
        switch (this.getOp()) {
        case SUB:
            stack.push(new TimespanLiteral(resultPos, 
                (int)((left.getValue().getTime() - right.getValue().getTime())) / 1000));
            break;
        default:
            this.invalidOperatorType(this.getOp());    
        }
    }
}
