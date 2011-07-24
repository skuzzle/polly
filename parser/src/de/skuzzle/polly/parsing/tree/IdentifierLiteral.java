package de.skuzzle.polly.parsing.tree;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;

public class IdentifierLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private java.lang.String identifier;
    
    public IdentifierLiteral(Token token) {
        super(token, Type.UNKNOWN);
        this.identifier = token.getStringValue();
    }
    
    
    
    public IdentifierLiteral(java.lang.String identifier) {
        super(new Token(TokenType.IDENTIFIER, Position.EMPTY, identifier), Type.UNKNOWN);
        this.identifier = identifier;
    }    
    
    
    public java.lang.String getIdentifier() {
        return this.identifier;
    }
    
    
    
    @Override
    public java.lang.String toString() {
        return this.identifier;
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
        IdentifierLiteral other = (IdentifierLiteral) obj;
        if (this.identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!this.identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }



    @Override
    public int compareTo(Literal o) {
        if (o instanceof IdentifierLiteral) {
            return this.identifier.compareTo(((IdentifierLiteral) o).identifier);
        }
        throw new RuntimeException("Not compareable");
    }
    
    
    
    @Override
    public Object clone() {
        IdentifierLiteral result = new IdentifierLiteral(this.identifier);
        result.setPosition(this.getPosition());
        result.setType(this.getType());
        return result;
    }
}