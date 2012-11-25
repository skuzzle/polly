package de.skuzzle.polly.parsing.ast.operators;

import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.Type;
import de.skuzzle.polly.parsing.util.Stack;



/**
 * This represents a casting operator. Casting operators are normal function calls
 * to a function which name equals the name of the cast target type.
 * 
 * @author Simon Taddiken
 *
 */
public class Cast extends Operator {

    protected final static String PARAM_NAME = "$param";

    
    
    /**
     * Creates a new Casting operator.
     * 
     * @param operator The operator type. Important: Must be one of the values from the
     *          'casting' section in <code>OpType</code>.
     * @param target Target type to cast to.
     */
    public Cast(OpType operator, Type target) {
        super(operator, target);
    }
    
    

    @Override
    public void execute(Stack<Literal> stack, Namespace ns, Visitor execVisitor)
            throws ASTTraversalException {
        
        // on a function call, parameters are already executed to be a Literal
        final Literal operand = (Literal) ns.resolveVar(
            new ResolvableIdentifier(Position.EMPTY, PARAM_NAME), 
            Type.ANY).getExpression();
        
        stack.push(operand.castTo(this.getType()));
    }



    @Override
    public Declaration createDeclaration() {
        // create parameter that accepts any expression (Type.ANY)
        final ResolvableIdentifier rid = 
            new ResolvableIdentifier(Position.EMPTY, PARAM_NAME);
        final Collection<Parameter> p = Arrays.asList(
            new Parameter[] {new Parameter(Position.EMPTY, rid, Type.ANY)});
        
        final FunctionLiteral func = new FunctionLiteral(Position.EMPTY, p, this);
        func.setType(new FunctionType(this.getType(), Parameter.asType(p)));
        func.setReturnType(this.getType());
        
        return new VarDeclaration(func.getPosition(), this.getType().getTypeName(), 
            func);
    }



    @Override
    public void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException { }
}
