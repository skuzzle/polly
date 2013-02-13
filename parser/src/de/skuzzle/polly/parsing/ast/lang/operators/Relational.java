package de.skuzzle.polly.parsing.ast.lang.operators;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;



public class Relational extends BinaryOperator<Literal, Literal> {

    private static final long serialVersionUID = 1L;
    
    public Relational(OpType id) {
        super(id);
        final TypeVar a = Type.newTypeVar("A");
        this.initTypes(Type.BOOLEAN, a, a);
    }



    @Override
    protected void resolve(Expression left, Expression right, Namespace ns,
            Visitor typeResolver) throws ASTTraversalException {

        if (!left.getUnique().isComparable()) {
            throw new ASTTraversalException(left.getPosition(), 
                "Typ '" + left.getUnique() + "' definiert keine Ordnung");
        }
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, Literal left, Literal right,
            Position resultPos, Visitor execVisitor) throws ASTTraversalException {
        
        final int comp = left.compareTo(right);
        switch (this.getOp()) {
        case EQ:
            stack.push(new BooleanLiteral(resultPos, comp == 0));
            break;
        case NEQ:
            stack.push(new BooleanLiteral(resultPos, comp != 0));
            break;
        case LT:
            stack.push(new BooleanLiteral(resultPos, comp < 0));
            break;
        case ELT:
            stack.push(new BooleanLiteral(resultPos, comp <= 0));
            break;
        case GT:
            stack.push(new BooleanLiteral(resultPos, comp > 0));
            break;
        case EGT:
            stack.push(new BooleanLiteral(resultPos, comp >= 0));
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
