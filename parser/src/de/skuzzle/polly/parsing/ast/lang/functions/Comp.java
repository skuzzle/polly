package de.skuzzle.polly.parsing.ast.lang.functions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;


public class Comp extends BinaryOperator<Literal, Literal> {

    private static final long serialVersionUID = 1L;

    
    public Comp() {
        super(OpType.COMP);
        final TypeVar tv = Type.newTypeVar("A");
        this.initTypes(Type.NUM, tv, tv);
    }

    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, Literal left, Literal right,
            Position resultPos, Visitor execVisitor) throws ASTTraversalException {
        stack.push(new NumberLiteral(resultPos, left.compareTo(right)));
    }

}
