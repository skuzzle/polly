package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.tools.collections.Stack;


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
