package de.skuzzle.polly.parsing.tree.functions;

import java.math.BigInteger;
import java.util.Stack;


import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.tree.literals.ListLiteral;
import de.skuzzle.polly.parsing.tree.literals.Literal;
import de.skuzzle.polly.parsing.tree.literals.NumberLiteral;
import de.skuzzle.polly.parsing.util.Fields;
import de.skuzzle.polly.parsing.util.Matrix;
import de.skuzzle.polly.parsing.util.MatrixUtils;



public class Functions {
    
    
    public final static class Contains extends ExpressionStub {


        private static final long serialVersionUID = 1L;

        public Contains() {
            super(Type.BOOLEAN);
        }
        
        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            Literal item = stack.pop();
            ListLiteral list = (ListLiteral) stack.pop();
            
            // no list/item type check. if checked for item of wrong type, the
            // result is just false, but no type error occurs.
            stack.push(new BooleanLiteral(list.getElements().contains(item)));
        }
        
    }
    
    
    
    public static class ListLength extends ExpressionStub {
        
        private static final long serialVersionUID = 1L;

        public ListLength() {
            super(Type.NUMBER);
        }
        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            ListLiteral lit = (ListLiteral) stack.pop();
            
            stack.push(new NumberLiteral(lit.getElements().size()));
        }
    }
    
    
    
    public static enum ListType {
        SUM, AVG, LENGTH;
    }
    
    
    
    public final static class List extends ExpressionStub {

        private static final long serialVersionUID = 1L;

        private ListType type;
        
        public List(ListType type) {
            super(Type.NUMBER);
            this.type = type;
        }
        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            ListLiteral lit = (ListLiteral) stack.pop();
            
            double sum = 0;
            switch (this.type) {
            case SUM:
                for (Expression e : lit.getElements()) {
                    sum += ((NumberLiteral) e).getValue();
                }
                stack.push(new NumberLiteral(sum));
                break;
            case AVG:
                for (Expression e : lit.getElements()) {
                    sum += ((NumberLiteral) e).getValue();
                }
                stack.push(new NumberLiteral(sum / lit.getElements().size()));
                break;
            }
        }
        
    }
    
    
    
    
    public final static class Random extends ExpressionStub {
        
        private static final long serialVersionUID = 1L;
        private final static java.util.Random random = new java.util.Random();

        public Random() {
            super(Type.NUMBER);
        }
        
        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            NumberLiteral upper = (NumberLiteral) stack.pop();
            NumberLiteral lower = (NumberLiteral) stack.pop();
            
            int start = lower.isInteger();
            int end = upper.isInteger();
            
            stack.push(new NumberLiteral(random.nextInt((end - start + 1) + start)));
        }
    }
    
    
    
    
    public final static class Binomial extends ExpressionStub {
        
        private static final long serialVersionUID = 1L;

        public Binomial() {
            super(Type.NUMBER);
        }
        
        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            NumberLiteral upper = (NumberLiteral) stack.pop();
            NumberLiteral lower = (NumberLiteral) stack.pop();
            
            BigInteger n = BigInteger.valueOf(lower.isInteger());
            BigInteger k = BigInteger.valueOf(upper.isInteger());
            
            stack.push(new NumberLiteral(binomialCoefficient(n, k).intValue()));
        }
        
        
        private static BigInteger binomialCoefficient(BigInteger n, BigInteger k){
            
            BigInteger n_minus_k=n.subtract(k);
            if(n_minus_k.compareTo(k)<0){
                BigInteger temp=k;
                k=n_minus_k;
                n_minus_k=temp;
            }
            
            BigInteger numerator=BigInteger.ONE;
            BigInteger denominator=BigInteger.ONE;
            
            for(BigInteger j=BigInteger.ONE; j.compareTo(k)<=0; j=j.add(BigInteger.ONE)){
                numerator=numerator.multiply(j.add(n_minus_k));
                denominator=denominator.multiply(j);
                BigInteger gcd=numerator.gcd(denominator);
                numerator=numerator.divide(gcd);
                denominator=denominator.divide(gcd);
            }
            
            return numerator;
        }
    }
    
    
    
    public static enum MatrixType {
        INT_MOD, DOUBLE;
    }
    
    
    
    public final static class GaussianEliminationModP extends ExpressionStub {

        private static final long serialVersionUID = 1L;
        
        public GaussianEliminationModP() {
            super(new de.skuzzle.polly.parsing.ListType(
                new de.skuzzle.polly.parsing.ListType(Type.NUMBER)));
        }
        
        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            NumberLiteral p = (NumberLiteral) stack.pop();
            ListLiteral list = (ListLiteral) stack.pop();
            
            if (!list.checkIsValidMatrix()) {
                throw new ExecutionException("Liste hat kein g�ltes Matrix-Format", 
                    list.getPosition());
            }
            
            Matrix<Integer> matrix = list.toIntMatrix(
                Fields.integerModulo(p.isInteger()));
            MatrixUtils.toGaussForm(matrix);
            stack.push(new ListLiteral(matrix));
        }
    }
    
    
    
    public final static class GaussianElimination extends ExpressionStub {

        private static final long serialVersionUID = 1L;
        
        public GaussianElimination() {
            super(new de.skuzzle.polly.parsing.ListType(
                new de.skuzzle.polly.parsing.ListType(Type.NUMBER)));
        }
        
        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            ListLiteral list = (ListLiteral) stack.pop();
            
            if (!list.checkIsValidMatrix()) {
                throw new ExecutionException("Liste hat kein g�ltes Matrix-Format", 
                    list.getPosition());
            }
            
            Matrix<Double> matrix = list.toDoubleMatrix();
            MatrixUtils.toGaussForm(matrix);
            stack.push(new ListLiteral(matrix));
        }
    }
    
    
    
    public static enum MathType {
        SIN, COS, TAN, ABS, SQRT, CEIL, FLOOR, LOG, ROUND, ATAN, ACOS, ASIN;
    }

    
    
    public final static class Math extends ExpressionStub {

        private static final long serialVersionUID = 1L;

        private MathType func;
        
        public Math(MathType func) {
            super(Type.NUMBER);
            this.func = func;
        }
        
        
        
        @Override
        public void collapse(Stack<Literal> stack) throws ExecutionException {
            NumberLiteral operand = (NumberLiteral) stack.pop();
            double result = Double.NaN;
            
            switch (this.func) {
            case SIN:
                result = java.lang.Math.sin(operand.getValue());
                break;
            case COS:
                result = java.lang.Math.cos(operand.getValue());
                break;
            case TAN:
                result = java.lang.Math.tan(operand.getValue());
                break;
            case ABS:
                result = java.lang.Math.abs(operand.getValue());
                break;
            case SQRT:
                result = java.lang.Math.sqrt(operand.getValue());
                break;
            case CEIL:
                result = java.lang.Math.ceil(operand.getValue());
                break;
            case FLOOR:
                result = java.lang.Math.floor(operand.getValue());
                break;
            case LOG:
                result = java.lang.Math.log(operand.getValue());
                break;
            case ROUND:
                result = java.lang.Math.round(operand.getValue());
                break;
            case ATAN:
                result = java.lang.Math.atan(operand.getValue());
                break;
            case ASIN:
                result = java.lang.Math.asin(operand.getValue());
                break;
            case ACOS:
                result = java.lang.Math.acos(operand.getValue());
                break;
            }
            
            stack.push(new NumberLiteral(result));
        }
    }
    
    
    
    /**
     * Private constructor.
     */
    private Functions() {}
}
