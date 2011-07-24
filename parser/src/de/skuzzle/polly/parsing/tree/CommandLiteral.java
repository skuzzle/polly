package de.skuzzle.polly.parsing.tree;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Token;
import de.skuzzle.polly.parsing.TokenType;
import de.skuzzle.polly.parsing.Type;

public class CommandLiteral extends Literal {

	private static final long serialVersionUID = 1L;
	private String commandName;
	
	
	
	public CommandLiteral(Token token) {
		super(token, Type.COMMAND);
		this.commandName = token.getStringValue();
	}
	
	
	
	public CommandLiteral(String commandName) {
		super(new Token(TokenType.COMMAND, Position.EMPTY, commandName), Type.COMMAND);
		this.commandName = commandName;
	}
	
	
	
	public String getCommandName() {
		return this.commandName;
	}
	
	
	
	@Override
	public Literal castTo(Type target) throws ExecutionException {
	    if (target.check(Type.STRING)) {
	        return new StringLiteral(this.getToken());
	    }
	    return super.castTo(target);
	}
	
	
	
	@Override
	public String toString() {
		return ":" + this.getCommandName();
	}
	
	

	@Override
	public int compareTo(Literal o) {
        if (o instanceof CommandLiteral) {
            return this.commandName.compareTo(((CommandLiteral) o).commandName);
        }
        throw new RuntimeException("Not compareable");
	}

	
	
	@Override
	public Object clone() {
		return new CommandLiteral(this.getToken());
	}
}
