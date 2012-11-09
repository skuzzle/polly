package de.skuzzle.polly.parsing.ast.declarations;

import java.util.ArrayList;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;


public class Parameter extends Node {
    
    public static Collection<Type> asType(Collection<Parameter> params) {
        final Collection<Type> result = new ArrayList<Type>(params.size());
        for (final Parameter p : params) {
            result.add(p.getType());
        }
        return result;
    }
    
    
    private final Type type;
    private ResolvableIdentifier name;
    
    
    
    public Parameter(Position position, Type type, ResolvableIdentifier name) {
        super(position);
        this.type = type;
        this.name = name;
    }



    public ResolvableIdentifier getName() {
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