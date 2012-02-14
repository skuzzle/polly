package de.skuzzle.polly.parsing.tree.literals;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;





public class NumberLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    private int radix = 10;
    
    public NumberLiteral(Token token) {
        super(token, Type.NUMBER);
    }
    
    
    public NumberLiteral(double value) {
    	this(value, Position.EMPTY);
    }
    
    
    public NumberLiteral(double value, Position position) {
        super(new Token(TokenType.NUMBER, position, value), Type.NUMBER);
    }
    
    
    
    public double getValue() {
        return this.getToken().getFloatValue();
    }
    
    
    
    public int getRadix() {
        return this.radix;
    }
    
    
    public void setRadix(int radix) {
        this.radix = radix;
    }
    
    
    
    @Override
    public Literal castTo(Type target) throws ExecutionException {
        if (target.check(Type.STRING)) {
            return new StringLiteral(this.toString(), this.getPosition());
        } else if (target.check(Type.TIMESPAN)) {
            return new TimespanLiteral((long)this.getValue(), this.getPosition());
        } else if (target.check(Type.DATE)) {
            return new DateLiteral(new Date((long) this.getValue()), this.getPosition());
        }
        return super.castTo(target);
    }
    
    
    private boolean isIntegerHelper() {
        int val = (int) this.getValue();
        return (double)val == this.getValue();
    }
    
    
    
    public int isInteger() throws ExecutionException {
        return this.isInteger(this.getPosition());
    }
    
    
    
    public int isInteger(Position pos) throws ExecutionException {
        int val = (int) this.getValue();
        if (!this.isIntegerHelper()) {
            throw new ExecutionException("'" + this.getValue() + "' ist keine Ganzzahl", 
                    pos);
        }
        return val;
    }
    
    
    
    public int nonZeroInteger() throws ExecutionException {
        this.nonZero();
        return this.isInteger();
    }
    
    
    
    public int nonZeroInteger(Position pos) throws ExecutionException {
        this.nonZero(pos);
        return this.isInteger(pos);
    }
    
    
    
    public double nonZero() throws ExecutionException {
        return this.nonZero(this.getPosition());
    }
    
    
    
    public double nonZero(Position pos) throws ExecutionException {
        if (this.getValue() == 0.0) {
            throw new ExecutionException("Division durch 0", pos);
        }
        
        return this.getValue();
    }
    
    
    
    @Override
    public String toString() {
        if (this.isIntegerHelper()) {
            return Integer.toString((int)this.getValue(), this.radix);
        }
        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
        nf.applyPattern("0.####");
        return nf.format(this.getValue());
    }


    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NumberLiteral other = (NumberLiteral) obj;
        if (Double.doubleToLongBits(this.getValue()) != 
                Double.doubleToLongBits(other.getValue())) {
            return false;
        }
        return true;
    }



    @Override
    public int compareTo(Literal o) {
        if (o instanceof NumberLiteral) {
            return Double.compare(this.getValue(), ((NumberLiteral) o).getValue());
        }
        throw new RuntimeException("Not compareable");
    }
}