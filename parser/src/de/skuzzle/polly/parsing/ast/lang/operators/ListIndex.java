package de.skuzzle.polly.parsing.ast.lang.operators;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.ListTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;


public class ListIndex extends BinaryOperator<ListLiteral, NumberLiteral> {

    private static final long serialVersionUID = 1L;
    
    public ListIndex(OpType id) {
        super(id);
        final TypeVar a = Type.newTypeVar("A");
        this.initTypes(a, new ListTypeConstructor(a), Type.NUM);
        this.setMustCopy(true);
    }
    
    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, ListLiteral left,
            NumberLiteral right, Position resultPos, Visitor execVisitor) 
                throws ASTTraversalException {
        
        switch (this.getOp()) {
        case INDEX:
            stack.push((Literal) left.getContent().get(right.isInteger()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
