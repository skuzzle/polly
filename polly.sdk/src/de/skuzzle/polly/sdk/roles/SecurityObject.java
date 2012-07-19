package de.skuzzle.polly.sdk.roles;

import java.util.Set;


public interface SecurityObject {

    // TODO comments. hint: readonly set!
    public Set<String> getRequiredPermission();
}