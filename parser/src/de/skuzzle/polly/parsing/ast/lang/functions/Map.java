package de.skuzzle.polly.parsing.ast.lang.functions;

import java.util.ArrayList;
import java.util.Arrays;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;


/**
 * <p>Function that takes a list and another function. It creates a new list by applying
 * the function to each element of the source list and adding the result to a new 
 * list.</p>
 * 
 * <p>Type information: <code>List&lt;B&gt; map(List&lt;A&gt;, \(B : A))</code></p>
 * 
 * @author Simon Taddiken
 */
public class Map extends BinaryOperator<ListLiteral, FunctionLiteral> {

    private static final long serialVersionUID = 1L;
    
    
    public Map() {
        super(OpType.MAP);
        
        final TypeVar a = Type.newTypeVar("A");
        final TypeVar b = Type.newTypeVar("B");
        this.initTypes(new ListTypeConstructor(b), 
            new ListTypeConstructor(a), 
            new MapTypeConstructor(new ProductTypeConstructor(a), b));
        this.setMustCopy(true);
    }



    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral left,
            FunctionLiteral right, Position resultPos, Visitor execVisitor) 
                throws ASTTraversalException {
        
        final ArrayList<Expression> result = new ArrayList<Expression>();
        for (final Expression exp : left.getContent()) {
            final Call call = new Call(Position.NONE, right, 
                Arrays.asList(new Expression[] {exp}), Position.NONE);
            
            call.visit(execVisitor);
            result.add(stack.pop());
        }
        stack.push(new ListLiteral(left.getPosition(), result));
    }
}
