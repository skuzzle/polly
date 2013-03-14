package de.skuzzle.polly.core.parser.ast.lang.operators;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.StringLiteral;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class StringIndex extends BinaryOperator<StringLiteral, NumberLiteral> {

    public StringIndex() {
        super(OpType.INDEX);
        this.initTypes(Type.STRING, Type.STRING, Type.NUM);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, StringLiteral left,
            NumberLiteral right, Position resultPos, ASTVisitor execVisitor)
                throws ASTTraversalException {
        
        switch (this.getOp()) {
        case INDEX:
            int index = right.isInteger();
            
            if (index < 0 || index >= left.getValue().length()) {
                // TODO: report problem
                throw new ASTTraversalException(resultPos, "Index ausserhalb des gültigen Bereichs: " + index);
            }
            
            stack.push(new StringLiteral(resultPos, "" + left.getValue().charAt(index)));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }

}
