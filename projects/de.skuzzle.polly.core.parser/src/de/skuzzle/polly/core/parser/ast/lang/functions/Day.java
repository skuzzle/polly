package de.skuzzle.polly.core.parser.ast.lang.functions;

import java.util.Calendar;
import java.util.Locale;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.DateLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.tools.collections.Stack;


public class Day extends UnaryOperator<DateLiteral> {

    
    public Day() {
        super(OpType.DAY);
        this.initTypes(Type.STRING, Type.DATE);
    }
    
    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, DateLiteral operand,
            Position resultPos) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case DAY:
            final Calendar c = Calendar.getInstance();
            c.setTime(operand.getValue());
            final String dayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, 
                Locale.getDefault());
            stack.push(new StringLiteral(resultPos, dayName));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
