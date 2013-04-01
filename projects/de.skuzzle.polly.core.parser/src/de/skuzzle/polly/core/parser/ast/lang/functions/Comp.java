package de.skuzzle.polly.core.parser.ast.lang.functions;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.core.parser.ast.lang.BinaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class Comp extends BinaryOperator<Literal, Literal> {

    public Comp() {
        super(OpType.COMP);
        final TypeVar tv = Type.newTypeVar("A");
        this.initTypes(Type.NUM, tv, tv);
    }

    
    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, Literal left, Literal right,
            Position resultPos, ExecutionVisitor execVisitor) throws ASTTraversalException {
        stack.push(new NumberLiteral(resultPos, left.compareTo(right)));
    }

}
