package de.skuzzle.polly.parsing.tree.functions;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.literals.Literal;


public class ExpressionStub extends Expression {

    private static final long serialVersionUID = 1L;

    public ExpressionStub(Type type) {
        super(Position.EMPTY);
        this.setType(type);
    }

    @Override
    public Expression contextCheck(Namespace context) throws ParseException {
        return this;
    }

    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {}
}
