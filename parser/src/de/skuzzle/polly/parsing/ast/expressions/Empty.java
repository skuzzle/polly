package de.skuzzle.polly.parsing.ast.expressions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;


/**
 * Expression that does nothing except to represent the type that has been set in the
 * Constructor.
 * 
 * It will be used to represent formal parameter types. Thus, if this instance 
 * represents a Function, it must pop one signature off the stack (if one exists) 
 * when being visited.
 * 
 * @author Simon Taddiken
 */
public class Empty extends Expression {

    private static final long serialVersionUID = 1L;

    

    public Empty(Type type, Position position) {
        super(position, type);
    }



    @Override
    public void visit(ASTVisitor visitor) throws ASTTraversalException {
    }
    
    
    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return null;
    }
}
