package de.skuzzle.polly.core.parser.ast.directives;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.TokenType;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.literals.DateLiteral;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;


public class DelayDirective extends Directive {

    private Expression targetTime;
    private DateLiteral result;
    
    
    
    public DelayDirective(Position position, Expression targetTime) {
        super(position, TokenType.DELAY);
        this.targetTime = targetTime;
    }
    
    
    
    public void setResult(DateLiteral result) {
        this.result = result;
    }
    
    
    
    public DateLiteral getResult() {
        return this.result;
    }
    
    
    
    public Expression getTargetTime() {
        return this.targetTime;
    }

    
    
    public void setTargetTime(Expression targetTime) {
        this.targetTime = targetTime;
    }


    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }



    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transform(this);
    }



    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        
        if (!this.targetTime.traverse(visitor)) {
            return false;
        }
        
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
}