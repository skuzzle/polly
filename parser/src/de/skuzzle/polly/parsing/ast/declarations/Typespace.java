package de.skuzzle.polly.parsing.ast.declarations;

import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


public class Typespace extends Namespace {

    private final static Typespace TYPES;
    static {
        try {
            TYPES = new Typespace(null);
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
    
    
    
    public Type resolveType(ResolvableIdentifier name) 
            throws ASTTraversalException {
        final Declaration decl = this.tryResolve(name);
        if (decl == null) {
            return new TypeVar(name);
        }
        return ((TypeDeclaration) decl).getType();
    }

}
