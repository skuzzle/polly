package de.skuzzle.polly.parsing.ast.lang.functions;

import java.util.ArrayList;
import java.util.Arrays;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListType;
import de.skuzzle.polly.parsing.ast.declarations.types.MapType;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductType;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;
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

    public Map() {
        super(OpType.MAP);
        
        final TypeVar a = Type.newTypeVar("A");
        final TypeVar b = Type.newTypeVar("B");
        this.initTypes(new ListType(b), 
            new ListType(a), 
            new MapType(new ProductType(a), b));
    }



    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral left,
            FunctionLiteral right, Position resultPos, ASTVisitor execVisitor) 
                throws ASTTraversalException {
        
        final ArrayList<Expression> result = new ArrayList<Expression>();
        for (final Expression exp : left.getContent()) {
            final Call call = new Call(Position.NONE, right, 
                new ProductLiteral(Position.NONE, Arrays.asList(new Expression[] {exp})));

            //TypeResolver.resolveAST(call, ns);
            call.visit(execVisitor);
            result.add(stack.pop());
        }
        final ListLiteral ll = new ListLiteral(left.getPosition(), result);
        ll.setUnique(this.getUnique());
        stack.push(ll);
    }
}
