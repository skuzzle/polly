package de.skuzzle.polly.parsing.ast.lang;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.parameters.FunctionParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.ListParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;


/**
 * Represents a hardcoded function in the AST. Those functions are fully compatible with
 * user created function declarations but provide basic operations like the one provided
 * by the {@link Math} class. Those can not be expressed using primitive polly 
 * expressions and thus need a special treatment.
 * 
 * @author Simon Taddiken
 * @see Native
 */
public abstract class Function extends Native {

    private static final long serialVersionUID = 1L;
    private final ResolvableIdentifier name;
    
    

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
     * Creates a {@link FunctionLiteral} which represents this hardcoded function.
     * 
     * @return A {@link FunctionLiteral}.
     */
    protected abstract FunctionLiteral createFunction();

    
    
    /**
     * <p>Creates a proper declaration for this operator. Additionally, sets this 
     * Function's type according to the return type of the {@link FunctionLiteral} 
     * created by {@link #createFunction()}. This method should be called only once per 
     * instance.</p>
     * 
     * <p>The created declaration will have the 'isPrimitive' flag set to true and the 
     * 'mustCopy' flag set according to whether the function's type contains type 
     * variables.</p>
     * 
     * @return A declaration for this function.
     */
    public Declaration createDeclaration() {
        final FunctionLiteral func = this.createFunction();
        final Declaration vd = new Declaration(func.getPosition(), this.name, func);
        this.setUnique(((MapTypeConstructor) func.getUnique()).getTarget());
        vd.setNative(true);
        vd.setMustCopy(Type.containsTypeVar(this.getUnique()));
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
        if (type instanceof ListTypeConstructor) {
            final ListTypeConstructor lt = (ListTypeConstructor) type;
            return new ListParameter(Position.NONE, name, lt.getSubType());
        } else if (type instanceof MapTypeConstructor) {
            final MapTypeConstructor ft = (MapTypeConstructor) type;
            return new FunctionParameter(Position.NONE, ft.getTarget(), 
                ft.getSource().getTypes(), name);
        } else {
            return new Parameter(Position.NONE, name, type);
        }
    }



    @Override
    public void execute(Stack<Literal> stack, Namespace ns, Visitor execVisitor)
        throws ASTTraversalException {}



    @Override
    public void resolveType(Namespace ns, Visitor typeResolver)
        throws ASTTraversalException {}
}
