package de.skuzzle.polly.core.parser.ast.lang.operators;

import java.util.ArrayList;
import java.util.Collections;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.ListType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.tools.collections.Stack;


public class UnaryList extends UnaryOperator<ListLiteral> {

    public UnaryList(OpType op) {
        super(op);
        final TypeVar a = Type.newTypeVar("A");
        this.initTypes(new ListType(a), new ListType(a));
    }

    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral operand,
            Position resultPos) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case EXCLAMATION:
            final ArrayList<Expression> tmp = 
                new ArrayList<Expression>(operand.getContent());
            final ListLiteral result = new ListLiteral(resultPos, tmp);
            Collections.reverse(tmp);
            result.addTypes(operand.getTypes());
            result.setUnique(operand.getUnique());
            stack.push(result);
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
