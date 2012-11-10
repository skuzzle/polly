package de.skuzzle.polly.parsing.ast.declarations;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;


public class Parameter extends Expression {
    
    public static Collection<Type> asType(Collection<Parameter> params) {
        final Collection<Type> result = new ArrayList<Type>(params.size());
        for (final Parameter p : params) {
            result.add(p.getType());
        }
        return result;
    }
    
    
    private final ResolvableIdentifier name;
    private final ResolvableIdentifier typeName;
    
    
    
    /**
     * Creates a parameter which' type is already known. 
     * 
     * @param position Position of the parameter in the input.
     * @param name Name of the parameter.
     * @param type Type of the parameter.
     */
    public Parameter(Position position, ResolvableIdentifier name, Type type) {
        super(position, type);
        this.name = name;
        this.typeName = new ResolvableIdentifier(type.getTypeName());
        
    }
    
    
    
    /**
     * Creates a parameter which' type is unknown and must be resolved during type
     * checking.
     * 
     * @param position Position of the parameter in the input.
     * @param typeName Name of the type. Actual type will be resolved using this name.
     * @param name Name of the parameter.
     */
    public Parameter(Position position, ResolvableIdentifier typeName, 
            ResolvableIdentifier name) {
        super(position);
        this.typeName = typeName;
        this.name = name;
    }

    
    
    /**
     * Gets the name of the type of this parameter. The actual type will be resolved
     * during type checking.
     * 
     * @return The type name of this parameter.
     */
    public ResolvableIdentifier getTypeName() {
        return this.typeName;
    }
    


    /**
     * Gets the name of this parameter.
     * 
     * @return Tje parameter name.
     */
    public ResolvableIdentifier getName() {
        return this.name;
    }


    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitParameter(this);
    }
}