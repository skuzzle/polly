package de.skuzzle.polly.sdk;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class Messages extends Constants {

    private final static String FAMILY = "de.skuzzle.polly.sdk.Translation";
    
    // Command
    public static String commandNoDescription;
    public static String commandSignatures;
    public static String commandNoSignature;
    public static String commandNoSignatureId;
    public static String commandSample;
    
    // Delayed Command
    public static String delayedCommandCantExecute;
    
    // Types
    public static String typesStringSample;
    
    static {
        Resources.init(FAMILY, Messages.class);
    }
}
