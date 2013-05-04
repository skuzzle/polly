package de.skuzzle.polly.core.parser.ast.lang.operators;

import java.util.ArrayList;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class TransposeOperator extends UnaryOperator<ListLiteral> {

    public TransposeOperator() {
        super(OpType.TRANSPOSE);
        final Type a = Type.newTypeVar("A").listOf().listOf();
        this.initTypes(a, a);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral operand,
            Position resultPos, ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        final int dimOuter = operand.getContent().size();
        if (operand.getContent().isEmpty() || !(operand.getContent().get(0) instanceof ListLiteral)) {
            throw new ASTTraversalException(resultPos, "Illegal matrix");
        }
        final ListLiteral inner = (ListLiteral) operand.getContent().get(0);
        final int dimInner = inner.getContent().size();
        
        
        final Expression[][] matrix = new Expression[dimInner][dimOuter];
        for (int x = 0; x < operand.getContent().size(); ++x) {
            final ListLiteral list = (ListLiteral) operand.getContent().get(x);
            if (list.getContent().size() != dimInner) {
                throw new ASTTraversalException(resultPos, "Illegal matrix");
            }
            for (int y = 0; y < list.getContent().size(); ++ y) {
                matrix[y][x] = list.getContent().get(y);
            }
        }
        
        ListLiteral result = new ListLiteral(resultPos, new ArrayList<Expression>());
        result.setUnique(matrix[0][0].getUnique().listOf().listOf());
        for (int x = 0; x < matrix.length; ++x) {
            ListLiteral resultInner = new ListLiteral(resultPos, new ArrayList<Expression>());
            resultInner.setUnique(matrix[x][0].getUnique().listOf());
            for(int y = 0; y < matrix[0].length; ++y) {
                resultInner.getContent().add(matrix[x][y]);
            }
            result.getContent().add(resultInner);
        }
        stack.push(result);
    }

}
