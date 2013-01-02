package de.skuzzle.polly.parsing.ast.expressions.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
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
    
    
    
    private Identifier name;
    private final Declaration decl;
    private final ResolvableType type;
    
    
    
    /**
     * Creates a parameter which' type is already known. 
     * 
     * @param position Position of the parameter in the input.
     * @param name Name of the parameter.
     * @param type Type of the parameter.
     */
    public Parameter(Position position, Identifier name, Type type) {
        super(position, type);
        this.getTypes().clear();
        this.name = name;
        this.type = null;
        this.decl = new Declaration(this.getPosition(), this.getName(), this);
    }
    
    
    
    /**
     * Creates a parameter which' type is unknown and must be resolved during type
     * checking.
     * 
     * @param position Position of the parameter in the input.
     * @param type The resolvable type of this parameter.
     * @param name Name of the parameter.
     */
    public Parameter(Position position, ResolvableType type, Identifier name) {
        super(position);
        this.type = type;
        this.name = name;
        this.decl = new Declaration(this.getPosition(), this.getName(), this);
        this.decl.setMustCopy(false);
    }
    
    
    
    /**
     * Gets the {@link ResolvableType} with which this parameter was declared. Will 
     * return <code>null</code> if parameter was created with a concrete type using
     * the constructor {@link #Parameter(Position, Identifier, Type)}.
     * 
     * @return The resolvable type of this parameter.
     */
    public ResolvableType getResolvabelType() {
        return this.type;
    }
    
    
    
    /**
     * Gets a declaration for this parameter which can be stored in a {@link Namespace}.
     * 
     * @return Tha declaration.
     */
    public Declaration getDeclaration() {
        return this.decl;
    }
    


    /**
     * Gets the name of this parameter.
     * 
     * @return The parameter name.
     */
    public Identifier getName() {
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
        return this.name.equals(other.name);
    }
}