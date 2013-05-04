package de.skuzzle.polly.core.parser.ast.lang.functions;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.declarations.types.TypeVar;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;



public class Id extends UnaryOperator<Literal> {

    public Id() {
        super(OpType.ID);
        final TypeVar tv = Type.newTypeVar("A");
        this.initTypes(tv, tv);
    }

    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, Literal operand,
            Position resultPos, ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        stack.push(operand);
    }

}
