package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.BooleanLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class UnaryBooleanArithmetic extends UnaryOperator<BooleanLiteral> {
    
    public UnaryBooleanArithmetic(OpType opType) {
        super(opType);
        this.initTypes(Type.BOOLEAN, Type.BOOLEAN);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, BooleanLiteral operand,
            Position resultPos, ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case EXCLAMATION:
            stack.push(new BooleanLiteral(resultPos, !operand.getValue()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
