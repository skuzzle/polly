package de.skuzzle.polly.core.parser.ast.declarations.types.unification;


public final class Unifiers {
    
    public final static Unifier newDefault(boolean subtyping) {
        return new HashMapTypeUnifier(subtyping);
    }

    
    
    public final static Unifier newUnionFindUnifier(boolean subtyping) {
        return new UnionFindTypeUnifier(subtyping);
    }
    
    
    public final static Unifier newHashMapUnifier(boolean subtyping) {
        return new HashMapTypeUnifier(subtyping);
    }
    
    
    private Unifiers() {
    }   
}