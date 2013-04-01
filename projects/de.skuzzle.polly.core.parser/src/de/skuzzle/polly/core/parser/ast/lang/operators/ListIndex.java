package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class ListIndex extends BinaryOperator<ListLiteral, NumberLiteral> {

    public ListIndex(OpType id) {
        super(id);
        final TypeVar a = Type.newTypeVar("A");
        this.initTypes(a, a.listOf(), Type.NUM);
    }
    
    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral left,
            NumberLiteral right, Position resultPos, ExecutionVisitor execVisitor) 
                throws ASTTraversalException {
        
        switch (this.getOp()) {
        case INDEX:
            final int index = right.isInteger();
            
            if (index < 0 || index >= left.getContent().size()) {
                // TODO: report problem
                throw new ASTTraversalException(resultPos, 
                    "Index ausserhalb des gültigen Bereichs: " + index);
            }
            
            stack.push((Literal) left.getContent().get(index));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
