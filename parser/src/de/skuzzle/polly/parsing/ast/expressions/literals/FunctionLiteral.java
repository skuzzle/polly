package de.skuzzle.polly.parsing.ast.expressions.literals;


import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class FunctionLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private final ArrayList<Declaration> formal;
    private Expression expression;
    
    
    public FunctionLiteral(Position position, Collection<Declaration> formal, 
            Expression expression) {
        super(position, Type.UNKNOWN);
        this.formal = new ArrayList<Declaration>(formal);
        this.expression = expression;
    }

    
    
    /**
     * Gets the {@link Expression} representing this function.
     * 
     * @return The expression.
     */
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    /**
     * Gets the declared formal parameters of this function.
     * 
     * @return Collection of formal parameters.
     */
    public ArrayList<Declaration> getFormal() {
        return this.formal;
    }
    
    

    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatFunction(this);
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitFunctionLiteral(this);
    }
    
    
    
    @Override
    public String toString() {
        return Unparser.toString(this);
    }
}