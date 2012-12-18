package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.tools.Equatable;


/**
 * Represents the declaration of a primitive type.
 * 
 * @author Simon Taddiken
 */
public class TypeDeclaration extends Declaration {
    
    private static final long serialVersionUID = 1L;
    
    private final Type type;
    
    public TypeDeclaration(Identifier name, Type type) {
        super(Position.NONE, name);
        this.type = type;
    }
    
    
    
    @Override
    public void visit(Visitor visitor) throws ASTTraversalException {
        // TODO: visit TypeDecl?
    }



    @Override
    public final Type getType() {
        return this.type;
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return TypeDeclaration.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return false;
    }
}
