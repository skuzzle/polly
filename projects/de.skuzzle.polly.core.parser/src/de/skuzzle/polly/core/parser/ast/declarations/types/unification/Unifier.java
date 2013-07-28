package de.skuzzle.polly.core.parser.ast.declarations.types.unification;

import de.skuzzle.polly.core.parser.ast.declarations.types.Substitution;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;

public interface Unifier {

    /**
     * Tests for structural equality of the given type expression in the context of this 
     * unifier instance.
     * 
     * @param first First type to check. 
     * @param second Second type to check.
     * @return A substitution for the type variables in first and second or 
     *          <code>null</code> if unification was not successful.
     */
    public abstract Substitution unify(Type first, Type second);



    /**
     * Tests for structural equality of the two given type expressions.
     * 
     * @param first First type.
     * @param second Second type.
     * @return Whether the first type is an instance of the second type
     */
    public abstract boolean tryUnify(Type first, Type second);

}