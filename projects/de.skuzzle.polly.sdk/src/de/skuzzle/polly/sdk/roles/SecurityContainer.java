package de.skuzzle.polly.sdk.roles;

import java.util.Set;


/**
 * Elements that contain subelements that require different permissions than the super
 * element may implement this interface in order to report all permissions of all
 * elements to the outside.
 *  
 * @author Simon
 * @since 0.9.1
 */
public interface SecurityContainer {

    /**
     * Gets a set of all permissions that this object contains somehow. This will be
     * used by polly when reporting permissions so that they are stored in the database.
     * 
     * @return A set of permissions.
     */
    public abstract Set<String> getContainedPermissions();
}