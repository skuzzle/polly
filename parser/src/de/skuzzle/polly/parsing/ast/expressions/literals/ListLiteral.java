package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;


public class ListLiteral extends Literal {
    
    private static final long serialVersionUID = 1L;
    
    private final List<Expression> content;
    

    public ListLiteral(Position position, List<Expression> content) {
        super(position, Type.UNKNOWN);
        this.content = content;
    }
    
    
    
    public ListLiteral(Position position, List<Expression> content, Type subType) {
        this(position, content);
        this.setUnique(subType.listOf());
    }
    
    
    
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
}
