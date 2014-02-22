package de.skuzzle.polly.dom.types;


public interface Substitution {
    /**
     * Applies the given substitution to this one, creating a new {@link Substitution}
     * instance. 
     * 
     * @param s The substitution to apply.
     * @return A new {@link Substitution} instance.
     */
    public Substitution subst(Substitution s);

    /**
     * Gets the substitute for the given variable. If this Substitution contains no 
     * substitute for the provided variable, the variable itself is returned..
     * 
     * @param v The variable to find a substitute for.
     * @return The substitute.
     */
    public Type getSubstitute(TypeVariable v);
}
