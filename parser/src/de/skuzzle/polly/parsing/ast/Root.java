package de.skuzzle.polly.parsing.ast;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.tools.streams.StringBuilderWriter;
import de.skuzzle.polly.tools.strings.IteratorPrinter;

/**
 * The root Node holds a collection of parsed expressions.
 * 
 * @author Simon Taddiken
 */
public final class Root extends Node {
    
    private static final long serialVersionUID = 1L;

    private final ArrayList<Expression> expressions;
    private Identifier command;
    private List<Literal> results;
    
    
    
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



    public void setResults(List<Literal> results) {
        this.results = results;
    }
    
    
    
    public List<Literal> getResults() {
        return this.results;
    }
    
    
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(":");
        b.append(this.command);
        if (this.results != null && !this.results.isEmpty()) {
            b.append(" ");
            IteratorPrinter.print(this.results, " ", 
                new PrintWriter(new StringBuilderWriter(b)));
        }
        return b.toString();
    }
}
