package de.skuzzle.polly.parsing.ast.lang.operators;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;


public class BinaryDotDot extends BinaryOperator<NumberLiteral, NumberLiteral> {

    private static final long serialVersionUID = 1L;
    
    /** Maximum size for generated ListLiterals */
    public final static int MAX_LIST_SIZE = 10000;
    
    
    
    /**
     * Creates a ListLiteral that contains numbers from <code>first</code> until
     * <code>second</code>
     * 
     * @param first First number in the generated list.
     * @param second Last number in the generated list.
     * @param step Step between each generated number.
     * @param resultPos Suitable position for the resulting literal.
     * @return New ListLiteral containing the sequence of numbers.
     * @throws ASTTraversalException If start or end definitions are illegal.
     */
    public static ListLiteral createSequence(NumberLiteral first, NumberLiteral second, 
            NumberLiteral step, Position resultPos) throws ASTTraversalException {
        
        double start = first.getValue();
        double end = second.getValue();
        double s = step.getValue();
        double values = start;
        
        if (start > end && s > 0.0) {
            throw new ASTTraversalException(resultPos, 
                    "Ung¸ltige Start- und Endindizes (von " + start + 
                    " nach " + end + ")");
        } else if (start > end) {
            double tmp = start;
            start = end;
            end = tmp;
        }
        
        double listSize = Math.abs(end - start) / s;
        if (listSize > MAX_LIST_SIZE) {
            throw new ASTTraversalException(resultPos, "Liste zu groﬂ. " + listSize +
                    " Eintr‰ge (Erlaubt: " + MAX_LIST_SIZE);
        }
        
        final List<Expression> content = new ArrayList<Expression>();
        
        while (start <= end) {
            content.add(new NumberLiteral(resultPos, values));
            values += s;
            start += Math.abs(s);
        }
        return new ListLiteral(resultPos, content);
    }
    

    
    public BinaryDotDot() {
        super(OpType.DOTDOT);
        this.initTypes(new ListTypeConstructor(Type.NUM), Type.NUM, Type.NUM);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, NumberLiteral left,
            NumberLiteral right, Position resultPos, Visitor execVisitor) 
                throws ASTTraversalException {
        
        switch (this.getOp()) {
        case DOTDOT:
            stack.push(createSequence(left, right, 
                new NumberLiteral(this.getPosition(), 1.0), resultPos));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
