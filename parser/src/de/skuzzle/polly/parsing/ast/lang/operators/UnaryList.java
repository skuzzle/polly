package de.skuzzle.polly.parsing.ast.lang.operators;

import java.util.ArrayList;
import java.util.Collections;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.UnaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.util.Stack;


public class UnaryList extends UnaryOperator<ListLiteral> {

    private static final long serialVersionUID = 1L;
    
    
    
    public UnaryList(OpType op) {
        super(op, new ListTypeConstructor(Type.newTypeVar("A")), 
            new ListTypeConstructor(Type.newTypeVar("A")));
        
        this.setMustCopy(true);
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
            stack.push(result);
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
