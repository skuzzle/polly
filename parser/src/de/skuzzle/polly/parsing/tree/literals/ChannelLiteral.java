package de.skuzzle.polly.parsing.tree.literals;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;


public class ChannelLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private java.lang.String channelName;
    
    public ChannelLiteral(Token token) {
        super(token, Type.CHANNEL);
        this.channelName = token.getStringValue();
    }
    
    
    
    public ChannelLiteral(String channel) {
        super(new Token(TokenType.CHANNEL, Position.EMPTY, channel), Type.CHANNEL);
        this.channelName = channel;
    }

    
    
    @Override
    public String toString() {
        return this.channelName;
    }
    
    
    
	public String getChannelName() {
        return this.channelName;
	}
	
	
	
	@Override
	public Literal castTo(Type target) throws ExecutionException {
	    if (target.check(Type.STRING)) {
	        return new StringLiteral(this.getToken());
	    } else if (target.check(Type.USER)) {
	        return new UserLiteral(this.getToken());
	    }
	    return super.castTo(target);
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
        ChannelLiteral other = (ChannelLiteral) obj;
        if (this.channelName == null) {
            if (other.channelName != null) {
                return false;
            }
        } else if (!this.channelName.equals(other.channelName)) {
            return false;
        }
        return true;
    }



    @Override
    public int compareTo(Literal o) {
        if (o instanceof ChannelLiteral) {
            return this.channelName.compareTo(((ChannelLiteral) o).channelName);
        }
        throw new RuntimeException("Not compareable");
    }
}
