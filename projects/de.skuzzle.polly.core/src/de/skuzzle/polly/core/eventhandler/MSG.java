package de.skuzzle.polly.core.eventhandler;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {
    
    private final static String FAMILY = "de.skuzzle.polly.core.eventhandler.Translation"; //$NON-NLS-1$

    public static String autoLogoff;
    public static String cmdExecError;
    public static String cmdUnknown;
    public static String cmdUnknownSignature;
    public static String cmdInsufficientRights;
    public static String cmdInternalError;
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
