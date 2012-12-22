package de.skuzzle.polly.parsing.ast.lang.operators;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.lang.TernaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;


public class TernaryDotDot extends 
        TernaryOperator<NumberLiteral, NumberLiteral, NumberLiteral> {

    private static final long serialVersionUID = 1L;


    public TernaryDotDot() {
        super(OpType.DOTDOT);
        this.initTypes(new ListTypeConstructor(Type.NUM), Type.NUM, Type.NUM, Type.NUM);
    }

    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, NumberLiteral first,
            NumberLiteral second, NumberLiteral third, Position resultPos, 
            Visitor execVisitor) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case DOTDOT:
            stack.push(BinaryDotDot.createSequence(first, second, third, resultPos));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
