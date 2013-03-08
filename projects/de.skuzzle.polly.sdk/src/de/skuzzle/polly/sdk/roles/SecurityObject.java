package de.skuzzle.polly.sdk.roles;

import java.util.Collections;
import java.util.Set;

/**
 * Access to SecurityObjects can and must be checked using the {@link RoleManager}. 
 * If a user does not have the required permissions, he should not be able to access
 * this object.
 * 
 * @author Simon
 * @since 0.9.1
 */
public interface SecurityObject {

    /**
     * <p>This reports all the required permissions to access this object. The result set
     * can be passed to the 
     * {@link RoleManager#hasPermission(de.skuzzle.polly.sdk.User, Set)} Method.</p>
     * 
     * <p>The resulting set should be made readonly using 
     * {@link Collections#unmodifiableSet(Set)}</p>
     * 
     * @return A set with required permission names. This should always be read only.
     */
    public Set<String> getRequiredPermission();
}