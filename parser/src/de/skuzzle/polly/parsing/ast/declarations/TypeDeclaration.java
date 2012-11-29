package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.expressions.Identifier;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.types.Type;


public class TypeDeclaration extends Declaration {
    
    private static final long serialVersionUID = 1L;
    
    private final Type type;
    
    
    public TypeDeclaration(Identifier name, Type type) {
        super(Position.EMPTY, name);
        this.type = type;
    }


    
    @Override
    public Type getType() {
        return this.type;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        // TODO: visit TypeDecl?
    }
}
