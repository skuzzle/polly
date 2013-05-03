package de.skuzzle.polly.core.parser.ast.expressions.literals;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;
import de.skuzzle.polly.core.parser.ast.visitor.Unparser;

/**
 * Represents a function literals. Those consists of a list of formal parameters
 * and a function body.
 * 
 * @author Simon Taddiken
 */
public class FunctionLiteral extends Literal {
    
    private ArrayList<Declaration> formal;
    private Expression body;
    
    
    
    /**
     * Creates a new FunctionLiteral.
     * 
     * @param position Position of this literal within the source.
     * @param formal List of formal parameters.
     * @param body Body of this function.
     */
    public FunctionLiteral(Position position, Collection<Declaration> formal, 
            Expression body) {
        super(position, Type.UNKNOWN);
        this.formal = new ArrayList<Declaration>(formal);
        this.body = body;
    }
    
    
    
    /**
     * Gets the {@link Expression} representing this function.
     * 
     * @return The expression.
     */
    public Expression getBody() {
        return this.body;
    }
    
    
    
    /**
     * Sets the {@link Expression} which represents the body of this function.
     * 
     * @param body The new body.
     */
    public void setBody(Expression body) {
        this.body = body;
    }
    
    
    
    /**
     * Gets the declared formal parameters of this function.
     * 
     * @return Collection of formal parameters.
     */
    public ArrayList<Declaration> getFormal() {
        return this.formal;
    }
    
    
    
    /**
     * Sets the list of declaration which represent the formal parameter of this
     * function.
     * 
     * @param formal New list of formal parameters.
     */
    public void setFormal(List<Declaration> formal) {
        if (formal instanceof ArrayList) {
            this.formal = (ArrayList<Declaration>) formal;
        } else {
            this.formal = new ArrayList<Declaration>(formal);
        }
    }
    
    

    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatFunction(this);
    }
    
    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformFunction(this);
    }
    
    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        for (final Declaration formal : this.formal) {
            if (!formal.traverse(visitor)) {
                return false;
            }
        }
        if (!this.body.traverse(visitor)) {
            return false;
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
    
    
    
    @Override
    public String toString() {
        return Unparser.toString(this);
    }
}