package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.TimespanLiteral;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class UnaryTimespan extends UnaryOperator<TimespanLiteral> {

    public UnaryTimespan() {
        super(OpType.SUB);
        this.initTypes(Type.TIMESPAN, Type.TIMESPAN);
    }
    

    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, TimespanLiteral operand,
            Position resultPos, ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case SUB:
            stack.push(new TimespanLiteral(resultPos, -operand.getSeconds()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
