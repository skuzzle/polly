package de.skuzzle.polly.parsing.ast.expressions.literals;


import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;


public class FunctionLiteral extends Literal {

    private static final long serialVersionUID = 1L;
    
    private final ArrayList<Parameter> formal;
    private final Expression expression;
    
    
    // semantical edge will be resolved during type resolval.
    private Type returnType;
    
    
    
    public FunctionLiteral(Position position, Collection<Parameter> formal, 
            Expression expression) {
        super(position, Type.UNKNOWN);
        this.formal = new ArrayList<Parameter>(formal);
        this.expression = expression;
        
        //Important: ANY type, otherwise call can not be resolved
        this.returnType = Type.ANY;  
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
    
    
    
    private final FunctionType createType(Type returnType, Collection<Parameter> formal) {
        final Collection<Type> signature = new ArrayList<Type>(formal.size());
        for (final Parameter f : formal) {
            signature.add(f.getType());
        }
        return new FunctionType(returnType, signature);
    }
    
    
    
    /**
     * Will always return a {@link FunctionType} created from current context information
     * of this literal.
     * 
     * @return an instance of {@link FunctionType}
     */
    @Override
    public Type getType() {
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
}