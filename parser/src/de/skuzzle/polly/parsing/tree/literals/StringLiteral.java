package de.skuzzle.polly.parsing.tree.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;

public class StringLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private java.lang.String value;
    
    
    
    public StringLiteral(Token token) {
        super(token, Type.STRING);
        this.value = token.getStringValue();
    }
    
    
    
    public StringLiteral(java.lang.String value) {
        super(new Token(TokenType.STRING, Position.EMPTY, value), Type.STRING);
        this.value = value;
    }
    
    
    
    public StringLiteral(String value, Position position) {
        super(new Token(TokenType.STRING, position, value), Type.STRING);
        this.value = value;
    }
    
    
    
    public String getValue() {
        return this.value;
    }
    
    
    
    @Override
    public String toString() {
        return this.value;
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
        StringLiteral other = (StringLiteral) obj;
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
        if (o instanceof StringLiteral) {
            return this.value.compareTo(((StringLiteral) o).value);
        }
        throw new RuntimeException("Not compareable");
    }
}