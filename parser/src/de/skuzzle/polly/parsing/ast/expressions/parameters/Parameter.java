package de.skuzzle.polly.parsing.ast.expressions.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.tools.Equatable;


/**
 * This class represents a formal parameter declaration for a function.
 * 
 * @author Simon Taddiken
 */
public class Parameter extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Converts a collection of Parameters into a Collection of {@link Type}s which can
     * be used to create {@link FunctionType}s.
     * 
     * @param params The input collection of Parameters.
     * @return A collection containing only the types of the parameters 
     *          (in the same order).
     */
    public static List<Type> asType(Collection<Parameter> params) {
        final List<Type> result = new ArrayList<Type>(params.size());
        for (final Parameter p : params) {
            result.add(p.getUnique());
        }
        return result;
    }
    
    
    
    private ResolvableIdentifier name;
    private ResolvableIdentifier typeName;
    
    
    
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
        this.typeName = new ResolvableIdentifier(type.getName());
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
    
    
    
    @Override
    public <T extends Node> void replaceChild(T current, T newChild) {
        if (current == this.name) {
            this.name = (ResolvableIdentifier) newChild;
        } else if (current == this.typeName) {
            this.typeName = (ResolvableIdentifier) newChild;
        } else {
            super.replaceChild(current, newChild);
        }
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
     * @return The parameter name.
     */
    public ResolvableIdentifier getName() {
        return this.name;
    }


    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitParameter(this);
    }

    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Parameter.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        final Parameter other = (Parameter) o;
        // compare names and typenames
        return this.name.equals(other.name)
            && this.getTypeName().equals(other.getTypeName());
    }
}