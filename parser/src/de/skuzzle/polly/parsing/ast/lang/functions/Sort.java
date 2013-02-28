package de.skuzzle.polly.parsing.ast.lang.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductType;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ProductLiteral;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;
import de.skuzzle.polly.parsing.util.Stack;


public class Sort extends BinaryOperator<ListLiteral, FunctionLiteral> {

    private static final long serialVersionUID = 1L;
    
    public Sort() {
        super(OpType.SORT);
        final TypeVar tv = Type.newTypeVar("A");
        this.initTypes(tv.listOf(), tv.listOf(), new ProductType(tv, tv).mapTo(Type.NUM));
    }
    
    

    @Override
    protected void exec(final Stack<Literal> stack, Namespace ns, ListLiteral left,
            final FunctionLiteral right, final Position resultPos, 
            final ASTVisitor execVisitor) throws ASTTraversalException {
        
        final Comparator<Expression> c = new Comparator<Expression>() {

            @Override
            public int compare(Expression l1, Expression l2) {
                final Call call = new Call(resultPos, right, 
                    new ProductLiteral(resultPos, 
                        Arrays.asList(new Expression[] {l1, l2})));
                
                try {
                    call.visit(execVisitor);
                } catch (ASTTraversalException e) {
                    throw new RuntimeException(e);
                }
                final NumberLiteral nl = (NumberLiteral) stack.pop();
                return (int) nl.getValue();
            }
            
        };
        
        final List<Expression> copy = new ArrayList<Expression>(left.getContent());
        Collections.sort(copy, c);
        final ListLiteral result = new ListLiteral(resultPos, copy);
        result.setUnique(left.getUnique());
        stack.push(result);
    }


}
