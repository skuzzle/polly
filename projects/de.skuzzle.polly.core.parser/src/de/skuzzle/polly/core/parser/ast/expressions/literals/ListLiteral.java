package de.skuzzle.polly.core.parser.ast.expressions.literals;

import java.util.List;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;
import de.skuzzle.polly.tools.Equatable;


/**
 * Represents a list literal which consists of a list of expressions which all have the
 * same types.
 * 
 * @author Simon Taddiken
 */
public class ListLiteral extends Literal {
    
    private final List<Expression> content;
    

    /**
     * Creates a new ListLiteral.
     * 
     * @param position Position of this literal within the source.
     * @param content List of expressions in this list literal.
     */
    public ListLiteral(Position position, List<Expression> content) {
        super(position, Type.UNKNOWN);
        this.content = content;
    }
    
    
    
    /**
     * Creates a new ListLiteral for which the type of its content is already known.
     * 
     * @param position Position of this literal within the source.
     * @param content List of expressions in this list literal.
     * @param subType Type of all expressions in the list.
     */
    public ListLiteral(Position position, List<Expression> content, Type subType) {
        this(position, content);
        this.setUnique(subType.listOf());
    }
    
    
    
    /**
     * Gets the content of this literal.
     * 
     * @return All expressions in this literal.
     */
    public List<Expression> getContent() {
        return this.content;
    }

    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        return super.castTo(type);
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatList(this);
    }
    
    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformList(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        for (final Expression exp : this.content) {
            if (!exp.traverse(visitor)) {
                return false;
            }
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return ListLiteral.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final ListLiteral other = (ListLiteral) o;
        return this.content.equals(other.content);
    }
}
