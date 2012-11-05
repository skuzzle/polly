package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.Visitor;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.types.Type;


public class Parameter extends Node {
    
    private final Type type;
    private Identifier name;
    
    
    
    public Parameter(Position position, Type type, Identifier name) {
        super(position);
        this.type = type;
        this.name = name;
    }



    public Identifier getName() {
        return this.name;
    }


    
    public Type getType() {
        return this.type;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        visitor.visitParameter(this);
    }
}