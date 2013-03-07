package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;


/**
 * Expression that does nothing except to represent the type that has been set in the
 * Constructor. It will be used to represent formal parameter types. 
 * 
 * @author Simon Taddiken
 */
public class Empty extends Expression {
    

    public Empty(Type type, Position position) {
        super(position, type);
    }



    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return true;
    }
    
    
    
    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return this;
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        return true;
    }
}
