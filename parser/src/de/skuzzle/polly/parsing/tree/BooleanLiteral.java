package de.skuzzle.polly.parsing.tree;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;


public class BooleanLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
   
    public BooleanLiteral(Token token) {
        super(token, Type.BOOLEAN);
    }
    
    

    public BooleanLiteral(boolean value) {
        super(new Token(value == true ? TokenType.TRUE : TokenType.FALSE,
                Position.EMPTY), Type.NUMBER);
    }
    
    
    
    @Deprecated
    public BooleanLiteral(boolean value, Position position) {
        super(new Token(value == true ? TokenType.TRUE : TokenType.FALSE,
                position), Type.NUMBER);
    }
    
    
    
    public boolean getValue() {
        return this.getToken().getType() == TokenType.TRUE;
    }
    
    
    @Override
    public Literal castTo(Type target) throws ExecutionException {
        if(target.check(Type.NUMBER)) {
            double value = this.getValue() ? 1.0 : 0.0;
            return new NumberLiteral(value, this.getPosition());
        } else if (target.check(Type.STRING)) {
            return new StringLiteral(Boolean.toString(this.getValue()), 
                this.getPosition());
        }
        return super.castTo(target);
    }
    
    
    @Override
    public String toString() {
        return "" + this.getValue();
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
        BooleanLiteral other = (BooleanLiteral) obj;
        if (this.getValue() != other.getValue()) {
            return false;
        }
        return true;
    }

    

    @Override
    public int compareTo(Literal o) {
        return 0; 
    }
    
    
    
    @Override
    public Object clone() {
        BooleanLiteral result = new BooleanLiteral(this.getToken());
        
        result.setPosition(this.getPosition());
        result.setType(this.getType());
        return result;
    }
}