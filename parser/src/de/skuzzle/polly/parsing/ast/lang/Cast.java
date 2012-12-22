package de.skuzzle.polly.parsing.ast.lang;

import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;



/**
 * This represents a casting operator. Casting operators are normal function calls
 * to a function which name equals the name of the cast target type.
 * 
 * @author Simon Taddiken
 *
 */
public class Cast extends Operator {

    private static final long serialVersionUID = 1L;
    
    protected final static ResolvableIdentifier PARAM_NAME = new ResolvableIdentifier(
        Position.NONE, "$param");

    
    private final Type operandType;
    
    /**
     * Creates a new Casting operator.
     * 
     * @param operator The operator type. Important: Must be one of the values from the
     *          'casting' section in <code>OpType</code>.
     * @param target Target type to cast to.
     */
    public Cast(OpType operator, Type target) {
        super(operator);
        this.setUnique(target);
        this.operandType = Type.newTypeVar();
    }
    
    

    @Override
    public void execute(Stack<Literal> stack, Namespace ns, Visitor execVisitor)
            throws ASTTraversalException {
        
        // on a function call, parameters are already executed to be a Literal
        final Literal operand = (Literal) ns.resolveVar(
            PARAM_NAME, 
            this.operandType).getExpression();
        
        stack.push(operand.castTo(this.getUnique()));
    }


    
    @Override
    protected FunctionLiteral createFunction() {
        // create parameter that accepts any expression (Type.ANY)
        final Collection<Parameter> p = Arrays.asList(
            new Parameter[] { 
                new Parameter(Position.NONE, PARAM_NAME, this.operandType) });
        
        final FunctionLiteral func = new FunctionLiteral(Position.NONE, p, this);
        func.setUnique(new MapTypeConstructor(
            new ProductTypeConstructor(this.operandType), this.getUnique()));
        return func;
    }
    
    

    @Override
    public Declaration createDeclaration() {
        final FunctionLiteral func = this.createFunction();
        final VarDeclaration vd = new VarDeclaration(
            func.getPosition(), this.getUnique().getName(), func);
        return vd;
    }



    @Override
    public void resolveType(Namespace ns, Visitor typeResolver)
        throws ASTTraversalException {}
}
