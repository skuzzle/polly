package de.skuzzle.polly.parsing.ast.lang;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.MapType;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Empty;
import de.skuzzle.polly.parsing.ast.expressions.Native;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;
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
        this.setUnique(((MapType) func.getUnique()).getTarget());
        vd.setNative(true);
        return vd;
    }
    
    
    
    /**
     * Creates a new Parameter from a given type and name.
     * 
     * @param type The parameters type.
     * @param name The parameters name.
     * @return A new parameter instance.
     */
    protected Declaration typeToParameter(Type type, ResolvableIdentifier name) {
        return new Declaration(name.getPosition(), name, new Empty(type, 
            name.getPosition()));
    }



    @Override
    public void execute(Stack<Literal> stack, Namespace ns, ASTVisitor execVisitor)
        throws ASTTraversalException {}



    @Override
    public void resolveType(Namespace ns, ASTVisitor typeResolver)
        throws ASTTraversalException {}
}
