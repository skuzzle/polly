package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class ReverseString extends UnaryOperator<StringLiteral> {

    public ReverseString() {
        super(OpType.EXCLAMATION);
        this.initTypes(Type.STRING, Type.STRING);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, StringLiteral operand,
            Position resultPos, ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case EXCLAMATION:
            final StringBuilder b = new StringBuilder(operand.getValue());
            b.reverse();
            stack.push(new StringLiteral(resultPos, b.toString()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
