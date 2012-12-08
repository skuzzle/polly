package de.skuzzle.polly.parsing.ast.lang;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Hardcoded;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.parameters.FunctionParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.ListParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;


/**
 * Represents a hardcoded function in the AST. Those functions are fully compatible with
 * user created function declarations but provide basic operations like the one provided
 * by the {@link Math} class. Those can not be expressed using primitive polly 
 * expressions and thus need a special treatment.
 * 
 * @author Simon Taddiken
 * @see Hardcoded
 */
public abstract class Function extends Hardcoded {

    private static final long serialVersionUID = 1L;
    private final ResolvableIdentifier name;
    private boolean mustCopy;
    
    

    /**
     * Creates a new function with the given name.
     * 
     * @param name This function's name. 
     */
    public Function(String name) {
        super(Type.UNKNOWN);
        this.name = new ResolvableIdentifier(Position.NONE, name);
    }
    
    
    
    /**
     * Sets whether the declaration created by {@link #createDeclaration()} for this 
     * function must be copied when being resolved.
     *  
     * @param mustCopy Whether declarations to this function shall be copied when being 
     *          resolved.
     */
    protected void setMustCopy(boolean mustCopy) {
        this.mustCopy = mustCopy;
    }
    
    
    
    /**
     * Gets whether the declaration created by {@link #createDeclaration()} for this 
     * function is set to 'mustCopy'.
     *  
     * @return Whether declarations to this function shall be copied when being resolved.
     */
    public boolean mustCopy() {
        return this.mustCopy;
    }
    
    
    
    /**
     * Creates a {@link FunctionLiteral} which represents this hardcoded function.
     * 
     * @return A {@link FunctionLiteral}.
     */
    protected abstract FunctionLiteral createFunction();

    
    
    /**
     * <p>Creates a proper declaration for this operator. Additionally, sets this 
     * Function's type according to the Type of the {@link FunctionLiteral} created by 
     * {@link #createFunction()}. This method should be called only once per instance.</p>
     * 
     * <p>The created declaration will have the 'isPrimitive' flag set to true and the 
     * 'mustCopy' flag set according to the value {@link #mustCopy()} returns.</p>
     * 
     * @return A declaration for this function.
     */
    public Declaration createDeclaration() {
        final FunctionLiteral func = this.createFunction();
        final VarDeclaration vd = new VarDeclaration(func.getPosition(), this.name, func);
        this.setType(func.getType());
        vd.setPrimitive(true);
        vd.setMustCopy(this.mustCopy());
        return vd;
    }
    
    
    
    /**
     * Creates a new Parameter from a given type and name. Depending on the type, either
     * a {@link ListParameter}, {@link FunctionParameter} or normal {@link Parameter} os
     * returned.
     * 
     * @param type The parameters type.
     * @param name The parameters name.
     * @return A new parameter instance.
     */
    protected Parameter typeToParameter(Type type, ResolvableIdentifier name) {
        if (type instanceof ListType) {
            final ListType lt = (ListType) type;
            return new ListParameter(Position.NONE, name, lt.getSubType());
        } else if (type instanceof FunctionType) {
            final FunctionType ft = (FunctionType) type;
            return new FunctionParameter(Position.NONE, ft.getReturnType(), 
                ft.getParameters(), name);
        } else {
            return new Parameter(Position.NONE, name, type);
        }
    }



    @Override
    public void execute(Stack<Literal> stack, Namespace ns, Visitor execVisitor)
        throws ASTTraversalException {
    }



    @Override
    public void resolveType(Namespace ns, Visitor typeResolver)
        throws ASTTraversalException {
    }
}
