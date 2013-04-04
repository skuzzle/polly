package de.skuzzle.polly.core.parser.ast;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.Problem;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.LiteralFormatter;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;
import de.skuzzle.polly.tools.streams.StringBuilderWriter;
import de.skuzzle.polly.tools.strings.IteratorPrinter;

/**
 * The root Node holds a collection of parsed expressions.
 * 
 * @author Simon Taddiken
 */
public final class Root extends Node {

    private ArrayList<Expression> expressions;
    private Identifier command;
    private List<Literal> results;
    private final boolean hasProblems;
    
    
    
    /**
     * Creates a new Root Node with given {@link Position} and a collection of parsed
     * expressions.
     * 
     * @param position The Node's position.
     * @param command Name of the parsed command.
     * @param expressions Collection of parsed expressions.
     * @param hasProblems Whether the AST contains {@link Problem} nodes.
     */
    public Root(Position position, Identifier command, 
            Collection<Expression> expressions, boolean hasProblems) {
        super(position);
        this.command = command;
        this.expressions = new ArrayList<Expression>(expressions);
        this.hasProblems = hasProblems;
    }
    
    
    
    /**
     * Sets the expressions of this root.
     * 
     * @param expressions The new expressions.
     */
    public void setExpressions(List<Expression> expressions) {
        if (expressions instanceof ArrayList) {
            this.expressions = (ArrayList<Expression>) expressions;
        } else {
            this.expressions = new ArrayList<Expression>(expressions);
        }
    }
    
    
    
    
    /**
     * Whether this AST contains problem nodes.
     * 
     * @return Whether this AST contains problem nodes.
     */
    public boolean hasProblems() {
        return this.hasProblems;
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

    

    /**
     * Sets the command named of this root.
     * 
     * @param command The command name.
     */
    public void setCommand(Identifier command) {
        this.command = command;
    }
    
    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Root transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformRoot(this);
    }

    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        
        if (!this.command.traverse(visitor)) {
            return false;
        }
        for (final Expression exp : this.expressions) {
            if (!exp.traverse(visitor)) {
                return false;
            }
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
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
                new IteratorPrinter.StringProvider<Literal>() {

                    @Override
                    public String toString(Literal o) {
                        return o.format(LiteralFormatter.DEFAULT);
                    }
                },
                new PrintWriter(new StringBuilderWriter(b)));
        }
        return b.toString();
    }
}
