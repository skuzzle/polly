package de.skuzzle.polly.parsing.tree.literals;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.types.Type;


public class UserLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private java.lang.String userName;
    
    
    
    public UserLiteral(Token token) {
        super(token, Type.USER);
        this.userName = token.getStringValue();
    }
    
    
    
    public UserLiteral(java.lang.String userName) {
        super(new Token(TokenType.USER, Position.EMPTY, userName), Type.USER);
        this.userName = userName;
    }

    
    
    @Override
    public Literal castTo(Type target) throws ExecutionException {
        if (target.check(Type.STRING)) {
            return new StringLiteral(this.getToken());
        }
        return super.castTo(target);
    }
    
    
    
    @Override
    public java.lang.String toString() {
        return this.userName;
    }
    
    
    
    public String getUserName() {
    	return this.userName;
    }
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((this.userName == null) ? 0 : this.userName.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserLiteral)) {
            return false;
        }
        UserLiteral other = (UserLiteral) obj;
        if (this.userName == null) {
            if (other.userName != null) {
                return false;
            }
        } else if (!this.userName.equals(other.userName)) {
            return false;
        }
        return true;
    }
    
    
    
    @Override
    public int compareTo(Literal o) {
        if (o instanceof UserLiteral) {
            return this.userName.compareTo(((UserLiteral) o).userName);
        }
        throw new RuntimeException("Not compareable");
    }
}
