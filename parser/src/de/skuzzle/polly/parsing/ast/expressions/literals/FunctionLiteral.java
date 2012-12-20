package de.skuzzle.polly.parsing.ast.expressions.literals;


import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;


public class FunctionLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private final ArrayList<Parameter> formal;
    private Expression expression;
    
    
    // semantical edge will be resolved during type resolval.
    private Type returnType;
    
    
    
    public FunctionLiteral(Position position, Collection<Parameter> formal, 
            Expression expression) {
        super(position, Type.UNKNOWN);
        this.formal = new ArrayList<Parameter>(formal);
        this.expression = expression;
    }
    
    
    
    @Override
    public <T extends Node> void replaceChild(T current, T newChild) {
        if (current == this.expression) {
            this.expression = (Expression) newChild;
        } else {
            for (int i = 0; i < this.formal.size(); ++i) {
                if (this.formal.get(i) == current) {
                    this.formal.set(i, (Parameter) newChild);
                    return;
                }
            }
            super.replaceChild(current, newChild);
        }
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
    public ArrayList<Parameter> getFormal() {
        return this.formal;
    }
    
    
    
    private final Type createType(Type returnType, Collection<Parameter> formal) {
        return new MapTypeConstructor(
            new ProductTypeConstructor(Parameter.asType(this.formal)), 
            returnType);
    }
    
    
    
    /**
     * Will always return a {@link MapTypeConstructor} created from current context 
     * information of this literal.
     * 
     * @return an instance of {@link MapTypeConstructor}
     */
    @Override
    public Type getUnique() {
        return this.createType(this.returnType, this.formal);
    }
    
    
    
    /**
     * Updates return type information for this function.
     * 
     * @param type New return type.
     */
    public void setReturnType(Type type) {
        this.returnType = type;
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