package de.skuzzle.polly.parsing.ast.expressions.literals;

import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class ListLiteral extends Literal {
    
    private static final long serialVersionUID = 1L;
    
    private final List<Expression> content;
    

    public ListLiteral(Position position, List<Expression> content) {
        super(position, new ListTypeConstructor(Type.newTypeVar()));
        this.content = content;
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
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitListLiteral(this);
    }
}
