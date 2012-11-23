package de.skuzzle.polly.parsing.ast;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;

/**
 * The root Node holds a collection of parsed expressions.
 * 
 * @author Simon Taddiken
 */
public final class Root extends Node {

    private final ArrayList<Expression> expressions;
    private final Identifier command;
    
    
    
    /**
     * Creates a new Root Node with given {@link Position} and a collection of parsed
     * expressions.
     * 
     * @param position The Node's position.
     * @param command Name of the parsed command.
     * @param expressions Collection of parsed expressions.
     */
    public Root(Position position, Identifier command, 
            Collection<Expression> expressions) {
        super(position);
        this.command = command;
        this.expressions = new ArrayList<Expression>(expressions);
    }
    
    
    
    /**
     * Gets a collection of parsed expressions.
     * 
     * @return The parsed expressions.
     */
    public Collection<Expression> getExpressions() {
        return this.expressions;
    }
    
    
    
    /**
     * Gets the name of the parsed command.
     * 
     * @return The command name.
     */
    public Identifier getCommand() {
        return this.command;
    }

    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitRoot(this);
    }
}
