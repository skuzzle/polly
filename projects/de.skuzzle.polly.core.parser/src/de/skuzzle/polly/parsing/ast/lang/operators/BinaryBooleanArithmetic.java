package de.skuzzle.polly.parsing.ast.lang.operators;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;
import de.skuzzle.polly.parsing.util.Stack;


public class BinaryBooleanArithmetic extends BinaryOperator<BooleanLiteral, BooleanLiteral> {

    public BinaryBooleanArithmetic(OpType id) {
        super(id);
        this.initTypes(Type.BOOLEAN, Type.BOOLEAN, Type.BOOLEAN);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, BooleanLiteral left,
            BooleanLiteral right, Position resultPos, ASTVisitor execVisitor)
                throws ASTTraversalException {
        
        switch (this.getOp()) {
        case BOOLEAN_AND:
            stack.push(
                new BooleanLiteral(resultPos, left.getValue() && right.getValue()));
            break;
        case BOOLEAN_OR:
            stack.push(
                new BooleanLiteral(resultPos, left.getValue() || right.getValue()));
            break;
        case XOR:
            stack.push(
                new BooleanLiteral(resultPos, left.getValue() ^ right.getValue()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
