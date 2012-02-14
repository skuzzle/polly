package de.skuzzle.polly.parsing.tree.operators;

import java.util.List;
import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ListType;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.literals.ListLiteral;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.NumberLiteral;



public class TernaryDotDotOperator extends TernaryOperatorOverload {

    private static final long serialVersionUID = 1L;
    private static int MAX_LIST_SIZE = 10000;
    
    
    public TernaryDotDotOperator() {
        super(TokenType.DOTDOT, Type.NUMBER, Type.NUMBER, Type.NUMBER,
                new ListType(Type.NUMBER));
    }
    
    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        NumberLiteral third = (NumberLiteral) stack.pop();
        NumberLiteral second = (NumberLiteral) stack.pop();
        NumberLiteral first = (NumberLiteral) stack.pop();
        
        switch(this.getOperatorType()) {
            case DOTDOT:
                double start = first.getValue();
                double end = second.getValue();
                double step = third.getValue();
                double values = start;
                
                if (start > end && step > 0.0) {
                    throw new ExecutionException("Ung¸ltige Start- und Endindizes (von " + 
                            start + " nach " + end + ")", 
                            new Position(first.getPosition(), second.getPosition()));
                } else if (start > end) {
                    double tmp = start;
                    start = end;
                    end = tmp;
                }                
                
                double listSize = Math.abs(end - start) / step;
                if (listSize > MAX_LIST_SIZE) {
                    throw new ExecutionException("Liste zu groﬂü. " + listSize + 
                            " Eintr‰ge (Erlaubt: " + MAX_LIST_SIZE, first.getPosition());
                }
                
                ListLiteral result = new ListLiteral(first.getToken());
                List<Expression> expressions = result.getElements();
                
                while (start <= end) {
                    expressions.add(new NumberLiteral(values, first.getPosition()));
                    values += step;
                    start += Math.abs(step);
                }

                stack.push(result);
        }
    }
}
