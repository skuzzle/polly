package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;


public class ListLiteral extends Literal {
    
    private final Collection<Expression> content;
    

    public ListLiteral(Position position, Collection<Expression> content) {
        super(position, ListType.ANY_LIST);
        this.content = content;
    }
    
    
    
    public Collection<Expression> getContent() {
        return this.content;
    }

    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        return null;
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return null;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitListLiteral(this);
    }
}
