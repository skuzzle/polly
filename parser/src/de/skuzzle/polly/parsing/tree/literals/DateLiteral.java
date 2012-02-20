package de.skuzzle.polly.parsing.tree.literals;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;



public class DateLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private Date value;
    
    public DateLiteral(Token token) {
        super(token, Type.DATE);
        this.value = token.getDateValue();
    }
    
    
    
    public DateLiteral(java.util.Date value) {
        this(new Token(TokenType.DATETIME, Position.EMPTY, value));
    }
    
    
    
    public DateLiteral(Date value, Position position) {
        this(new Token(TokenType.DATETIME, position, value));
    }
    
    
    
    public Date getValue() {
        return this.value;
    }
    
    
    
    @Override
    public Literal castTo(Type target) throws ExecutionException {
        if (target.check(Type.NUMBER)) {
            return new NumberLiteral(this.value.getTime(), this.getPosition());
        }
        return super.castTo(target);
    }
    
    
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(this.value);
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
        DateLiteral other = (DateLiteral) obj;
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }



    @Override
    public int compareTo(Literal o) {
        if (o instanceof DateLiteral) {
            return this.value.compareTo(((DateLiteral) o).value);
        }
        throw new RuntimeException("Not compareable");
    }
}