package de.skuzzle.polly.parsing.tree.operators;

import java.util.Collections;
import java.util.Random;
import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.tree.literals.ListLiteral;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.NumberLiteral;
import de.skuzzle.polly.parsing.tree.literals.StringLiteral;
import de.skuzzle.polly.parsing.tree.literals.TimespanLiteral;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;


public class UnaryOperators {
    
    
    
    public static class TimespanOperator extends UnaryOperatorOverload {

        private static final long serialVersionUID = 1L;

        public TimespanOperator() {
            super(TokenType.SUB, Type.TIMESPAN, Type.TIMESPAN);
        }

        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            TimespanLiteral left = (TimespanLiteral) stack.pop();
            
            switch(this.getOperatorType()) {
                case SUB:
                    stack.push(new TimespanLiteral(-left.getValue()));
                    break;
            }
        }
    }
    
    
    /**
     * Takes one {@code STRING} parameter and returns a {@code STRING}.
     * @author Simon
     *
     */
    public static class StringOperator extends UnaryOperatorOverload {

        private static final long serialVersionUID = 1L;
        
        public StringOperator(TokenType operator) {
            super(operator, Type.STRING, Type.STRING);
        }
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            StringLiteral left = (StringLiteral) stack.pop();
            
            switch(this.getOperatorType()) {
                case EXCLAMATION:
                    stack.push(new StringLiteral(this.reverse(left.getValue())));
                    break;
            }
        }       
        
        private String reverse(String s) {
            StringBuilder result = new StringBuilder(s);
            return result.reverse().toString();
        }  
    }
    
    
    
    /**
     * Takes one parameter of type {@code LIST(of ANY)} and returns a {@code LIST} of the
     * same type.
     * @author Simon
     *
     */
    public static class ListOperator extends UnaryOperatorOverload {

        private static final long serialVersionUID = 1L;
        
        public ListOperator(TokenType operator) {
            super(operator, ListType.ANY_LIST, new ListType(Type.UNKNOWN));
        }
        
        @Override
        public void contextCheck(Namespace context, Expression expression)
                throws ParseException {
            super.contextCheck(context, expression);
            
            ListType t = ((ListType) expression.getType());
            this.setReturnType(t);
        }
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            ListLiteral left = (ListLiteral) stack.pop();
            
            switch(this.getOperatorType()) {
                case EXCLAMATION:
                    Collections.reverse(left.getElements());
                    stack.push(new ListLiteral(left.getElements()));
                    break;
            }
        }
    }
    
    
    
    /**
     * Takes one {@code BOOLEAN} parameter and returns a {@code BOOLEAN}.
     * @author Simon
     *
     */
    public static class BooleanOperator extends UnaryOperatorOverload {

        private static final long serialVersionUID = 1L;
        
        public BooleanOperator(TokenType operator) {
            super(operator, Type.BOOLEAN, Type.BOOLEAN);
        }

        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            BooleanLiteral left = (BooleanLiteral) stack.pop();
            
            switch(this.getOperatorType()) {
                case EXCLAMATION:
                    stack.push(new BooleanLiteral(!left.getValue()));
                    break;
            }
        }
    }
    
    
    
    /**
     * Takes one parameter of type {@code NUMBER} and returns a {@code NUMBER}.
     * @author Simon
     *
     */
    public static class ArithmeticOperator extends UnaryOperatorOverload {

        private static final long serialVersionUID = 1L;

        public ArithmeticOperator(TokenType operator) {
            super(operator, Type.NUMBER, Type.NUMBER);
        }

        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            NumberLiteral left = (NumberLiteral) stack.pop();
            
            switch(this.getOperatorType()) {
                case SUB:
                    stack.push(new NumberLiteral(-left.getValue(), left.getPosition()));
                    break;
            }
        }
    }
    
    
    
    public static class RandomListIndexOperator extends UnaryOperatorOverload {

        private static final long serialVersionUID = 1L;
        
        private static Random randomizer = new Random();
        
        public RandomListIndexOperator(TokenType type) {
            super(type, new ListType(Type.ANY), Type.ANY);
        }
        
        @Override
        public void contextCheck(Namespace context, Expression left) throws ParseException {
            super.contextCheck(context, left);
            
            ListType t = ((ListType) left.getType());
            this.setReturnType(t.getSubType());
        }

        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            ListLiteral left = (ListLiteral) stack.pop();
            
            int i = 0;
            switch(this.getOperatorType()) {
                case QUESTION:
                    i = randomizer.nextInt(left.getElements().size());
                    stack.push((Literal) left.getElements().get(i));
                    break;
                case QUEST_EXCALAMTION:
                    int size = left.getElements().size() - 1;
                    double gr = 2.0;
                    double g = Math.max(Math.min(randomizer.nextGaussian(), gr), -gr);
                    double f = ((g + gr) / (2.0 * gr));
                    i = (int) Math.round(f * (double) size);
                    stack.push((Literal) left.getElements().get(i));
            }
        }
    }
}