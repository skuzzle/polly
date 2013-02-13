package de.skuzzle.polly.parsing.ast.lang.operators;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;


public class BinaryStringArithmetic extends BinaryOperator<StringLiteral, StringLiteral> {

    private static final long serialVersionUID = 1L;

    public BinaryStringArithmetic(OpType id) {
        super(id);
        this.initTypes(Type.STRING, Type.STRING, Type.STRING);
    }

    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, StringLiteral left,
            StringLiteral right, Position resultPos, Visitor execVisitor)
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
