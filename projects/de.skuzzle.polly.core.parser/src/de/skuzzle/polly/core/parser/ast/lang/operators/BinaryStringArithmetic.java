package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class BinaryStringArithmetic extends BinaryOperator<StringLiteral, StringLiteral> {

    public BinaryStringArithmetic(OpType id) {
        super(id);
        this.initTypes(Type.STRING, Type.STRING, Type.STRING);
    }

    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, StringLiteral left,
            StringLiteral right, Position resultPos, ExecutionVisitor execVisitor)
            throws ASTTraversalException {
        
        switch (this.getOp()) {
        case ADD:
            stack.push(new StringLiteral(resultPos, left.getValue() + right.getValue()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
    
    

}
