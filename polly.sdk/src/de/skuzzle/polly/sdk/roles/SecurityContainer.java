package de.skuzzle.polly.sdk.roles;

import java.util.Set;


public interface SecurityContainer {

    public abstract Set<String> getContainedPermissions();
}