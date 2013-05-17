package de.skuzzle.polly.sdk.constraints;


/**
 * AttributeConstraints are used to constraint an user attribute to certain values.
 * 
 * @author Simon
 * @since 0.7
 */
public interface AttributeConstraint {

    /**
     * This method is called by polly before setting the attribute to value. When this
     * method returns <code>false</code>, the value can not be set and the method 
     * {@link UserManager#setAttributeFor(de.skuzzle.polly.sdk.User, String, 
     * String)} will throw a {@link ConstraintException}.
     * 
     * @param value
     *          The value to check.
     * @return
     *          Whether the value matches this constraint and can thus be set for
     *          the attribute.
     */
    public abstract boolean accept(String value);
}