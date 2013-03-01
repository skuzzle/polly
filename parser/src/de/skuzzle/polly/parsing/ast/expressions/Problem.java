package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.MissingType;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;


public class Problem extends Expression {
    
    private static int id;
    
    public Problem(Position position) {
        super(position, new MissingType(
            new Identifier(position, "$problem_" + (id++))));
    }
    
    
    
    @Override
    public boolean isProblem() {
        return false;
    }
    
    

    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }

    
    
    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return this;
    }

    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        return visitor.before(this) != ASTTraversal.ABORT 
            && visitor.after(this) == ASTTraversal.CONTINUE;
    }

}
