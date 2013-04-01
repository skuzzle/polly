package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.DateLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class DateTimespanArithmetic extends BinaryOperator<DateLiteral, TimespanLiteral>{

    public DateTimespanArithmetic(OpType id) {
        super(id);
        this.initTypes(Type.DATE, Type.DATE, Type.TIMESPAN);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, DateLiteral left,
            TimespanLiteral right, Position resultPos, ExecutionVisitor execVisitor) 
                throws ASTTraversalException {
        
        switch (this.getOp()) {
            case ADD:
                stack.push(new DateLiteral(resultPos, right.addToDate(left.getValue())));
                break;
            case SUB:
                final TimespanLiteral tmp = new TimespanLiteral(right.getPosition(), 
                    -right.getSeconds());
                stack.push(new DateLiteral(resultPos, tmp.addToDate(left.getValue())));
                break;
            default:
                this.invalidOperatorType(this.getOp());
        }
    }
    

}
