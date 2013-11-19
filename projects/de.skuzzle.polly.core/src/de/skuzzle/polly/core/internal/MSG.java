package de.skuzzle.polly.core.internal;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {
    
    private final static String FAMILY = "de.skuzzle.polly.core.internal.Translation"; //$NON-NLS-1$

    public static String autoLogonDescription;
    public static String moduleDefinitionNotFound;
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
