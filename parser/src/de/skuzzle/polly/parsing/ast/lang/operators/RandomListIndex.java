package de.skuzzle.polly.parsing.ast.lang.operators;

import java.util.Random;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.UnaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.util.Stack;


/**
 * Operators that choose random entries from a {@link ListLiteral}.
 * 
 * @author Simon Taddiken
 */
public class RandomListIndex extends UnaryOperator<ListLiteral> {

    private static final long serialVersionUID = 1L;
    private final static Random RANDOM = new Random();
    
    
    
    public RandomListIndex(OpType op) {
        super(op);
        final TypeVar a = Type.newTypeVar("A");
        this.initTypes(a, new ListTypeConstructor(a));
        this.setMustCopy(true);
    }

    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral operand,
            Position resultPos) throws ASTTraversalException {

        int i;
        switch (this.getOp()) {
        case QUESTION:
            i = RANDOM.nextInt(operand.getContent().size());
            stack.push((Literal) operand.getContent().get(i));
            break;
        case QUEST_EXCL:
            final int size = operand.getContent().size() - 1;
            final double gr = 2.0;
            final double g = Math.max(Math.min(RANDOM.nextGaussian(), gr), -gr);
            final double f = ((g + gr) / (2.0 * gr));
            i = (int) Math.round(f * (double) size);
            stack.push((Literal) operand.getContent().get(i));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
