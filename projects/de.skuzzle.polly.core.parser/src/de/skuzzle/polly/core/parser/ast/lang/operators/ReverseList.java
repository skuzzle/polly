package de.skuzzle.polly.core.parser.ast.lang.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class ReverseList extends UnaryOperator<ListLiteral> {

    public ReverseList() {
        super(OpType.EXCLAMATION);
        final TypeVar a = Type.newTypeVar();
        this.initTypes(a.listOf(), a.listOf());
    }

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral operand,
            Position resultPos, ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case EXCLAMATION:
            final List<Expression> op = new ArrayList<Expression>(operand.getContent());
            Collections.reverse(op);
            final ListLiteral r = new ListLiteral(resultPos, op);
            r.setUnique(operand.getUnique());
            r.setTypes(operand.getTypes());
            stack.push(r);
            
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
