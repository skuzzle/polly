package de.skuzzle.polly.core.parser.ast.lang.functions;

import java.util.ArrayList;
import java.util.Arrays;


import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.ProductType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


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
        this.initTypes(b.listOf(), 
            a.listOf(), 
            new ProductType(a).mapTo(b));
    }

    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral left,
            FunctionLiteral right, Position resultPos, ExecutionVisitor execVisitor) 
                throws ASTTraversalException {
        
        final ArrayList<Expression> result = new ArrayList<Expression>();
        for (final Expression exp : left.getContent()) {
            final Call call = new Call(Position.NONE, right, 
                new ProductLiteral(Position.NONE, Arrays.asList(new Expression[] {exp})));

            //TypeResolver.resolveAST(call, ns, execVisitor.getReporter());
            call.visit(execVisitor);
            result.add(stack.pop());
        }
        final ListLiteral ll = new ListLiteral(left.getPosition(), result);
        ll.setUnique(this.getUnique());
        stack.push(ll);
    }
}
