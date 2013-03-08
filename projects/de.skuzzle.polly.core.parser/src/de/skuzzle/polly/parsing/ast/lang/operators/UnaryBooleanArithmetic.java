package de.skuzzle.polly.parsing.ast.lang.operators;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.UnaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.tools.collections.Stack;


public class UnaryBooleanArithmetic extends UnaryOperator<BooleanLiteral> {
    
    public UnaryBooleanArithmetic(OpType opType) {
        super(opType);
        this.initTypes(Type.BOOLEAN, Type.BOOLEAN);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, BooleanLiteral operand,
            Position resultPos) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case EXCLAMATION:
            stack.push(new BooleanLiteral(resultPos, !operand.getValue()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
