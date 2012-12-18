package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class Typespace extends Namespace {

    private final static Typespace TYPES = new Typespace(null);
    static {
        try {
        TYPES.declare(Type.NUM.declaration());
        TYPES.declare(Type.STRING.declaration());
        TYPES.declare(Type.CHANNEL.declaration());
        TYPES.declare(Type.DATE.declaration());
        TYPES.declare(Type.TIMESPAN.declaration());
        TYPES.declare(Type.USER.declaration());
        } catch (ASTTraversalException e) {
            throw new RuntimeException("impossibru", e);
        }
    }
    
    
    
    public Typespace() {
        this(TYPES);
    }
    
    
    
    public Typespace(Typespace parent) {
        super(parent);
    }
    
    
    
    @Override
    public void declare(Declaration decl) throws ASTTraversalException {
        if (!(decl instanceof TypeDeclaration)) {
            throw new ASTTraversalException(
                decl.getPosition(), "Nur Typdeklarationen möglich");
        }
        super.declare(decl);
    }
    
    
    
    public TypeDeclaration resolveType(ResolvableIdentifier name) 
            throws ASTTraversalException {
        final Declaration decl = this.tryResolve(name);
        return (TypeDeclaration) decl;
    }

}
