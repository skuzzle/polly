package de.skuzzle.polly.core.parser.ast.lang.functions;

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
import de.skuzzle.polly.core.parser.ast.lang.TernaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.tools.collections.Stack;



/**
 * <p>Fold left function for list literals. It takes a list, a function and an argument. 
 * Then, the function starts with the argument as $result and subsequently stores the 
 * application of the function with parameters $result and the next list element back
 * to $result.</p>
 * 
 * <p>Type information: <code>A foldLeft(List&lt;A&gt;, \(A: A, B), A)</code></p>
 *   
 * @author Simon Taddiken
 */
public class FoldLeft extends TernaryOperator<ListLiteral, FunctionLiteral, Literal> {

    
    public FoldLeft() {
        super(OpType.FOLD_LEFT);
        final TypeVar a = Type.newTypeVar("A");
        final TypeVar b = Type.newTypeVar("B");
        this.initTypes(a, b.listOf(), a.mapFrom(new ProductType(a, b)), a);
    }



    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral first,
            FunctionLiteral second, Literal third, Position resultPos, ASTVisitor execVisitor) 
            throws ASTTraversalException {

        
        Literal result = third;
        for (final Expression exp : first.getContent()) {
            final Call call = new Call(Position.NONE, second, 
                new ProductLiteral(Position.NONE, 
                    Arrays.asList(new Expression[] {result, exp})));
        
            call.visit(execVisitor);
            result = stack.pop();
        }
        stack.push(result);
    }

}
