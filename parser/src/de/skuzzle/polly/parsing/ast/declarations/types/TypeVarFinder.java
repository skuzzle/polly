package de.skuzzle.polly.parsing.ast.declarations.types;


/**
 * Checks whether a type expression contains a TypeVar.
 * 
 * @author Simon Taddiken
 */
final class TypeVarFinder extends DefaultTypeVisitor {

    
    /**
     * Checks whether the given type expression contains a {@link TypeVar}.
     * 
     * @param type Type expression to check.
     * @return <code>true</code> iff the expression contains a {@link TypeVar}.
     */
    public final static boolean containsTypeVar(Type type) {
        final TypeVarFinder tvf = new TypeVarFinder();
        type.visit(tvf);
        return tvf.found;
    }
    
    
    
    /** Not instantiable from outside */
    public TypeVarFinder() {}
    
    
    
    private boolean found;
    @Override
    public void beforeVar(TypeVar v) {
        this.found = true;
        this.abort();
    }
}
