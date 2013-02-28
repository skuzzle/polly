package de.skuzzle.polly.parsing.ast.lang.functions;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.lang.UnaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.util.Stack;



public class Id extends UnaryOperator<Literal> {

    public Id() {
        super(OpType.ID);
        final TypeVar tv = Type.newTypeVar("A");
        this.initTypes(tv, tv);
    }

    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, Literal operand,
            Position resultPos) throws ASTTraversalException {
        
        stack.push(operand);
    }

}
