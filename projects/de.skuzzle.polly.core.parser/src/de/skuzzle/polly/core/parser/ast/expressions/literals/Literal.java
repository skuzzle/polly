package de.skuzzle.polly.core.parser.ast.expressions.literals;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;

/**
 * Literals are the source code representations of {@link Type types}. They therefore hold
 * one value of a certain type.
 * 
 * @author Simon Taddiken
 */
public abstract class Literal extends Expression implements Comparable<Literal> {
    
    /**
     * Creates a new Literal with given {@link Position} and {@link Type}.
     * 
     * @param position The position of the literal.
     * @param type The type of the literal.
     */
    Literal(Position position, Type type) {
        super(position, type);
    }
    
    
    
    /**
     * Tries to convert this literal into a new literal of the given type.
     * 
     * @param type Target type.
     * @return A new Literal with the given type and converted value.
     * @throws ASTTraversalException If this literal can not be casted to the given type.
     */
    public Literal castTo(Type type) throws ASTTraversalException {
        if (type == this.getUnique()) {
            return this;
        }
        if (type.equals(Type.STRING)) {
            return new StringLiteral(this.getPosition(), 
                this.format(LiteralFormatter.DEFAULT));
        }
        throw new ASTTraversalException(this.getPosition(), 
            this.getUnique().getName() + " kann nicht zu " + type.getName() + 
            " gecastet werden");
    }

    
    
    /**
     * Formats the value of this literal as String.
     * 
     * @param formatter The formatter used to format the literals with. 
     * @return Formatted value of this literal.
     */
    public abstract String format(LiteralFormatter formatter);
    
    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
    
    
    
    @Override
    public int compareTo(Literal o) {
        throw new UnsupportedOperationException(
            this.getClass().getSimpleName() + " is not comparable");
    }
    
    
    
    @Override
    public String toString() {
        return this.format(LiteralFormatter.DEFAULT);
    }
}
