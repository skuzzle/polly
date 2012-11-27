package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;


public abstract class Literal extends Expression implements Comparable<Literal> {

    private static final long serialVersionUID = 1L;
    
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
        throw new ASTTraversalException(this.getPosition(), 
            this.getType().getTypeName() + " kann nicht zu " + type.getTypeName() + 
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
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitLiteral(this);
    }
    
    
    
    @Override
    public int compareTo(Literal o) {
        throw new UnsupportedOperationException(
            this.getClass().getSimpleName() + " is not comparable");
    }
}
