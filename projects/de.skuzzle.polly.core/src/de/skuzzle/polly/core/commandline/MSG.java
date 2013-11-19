package de.skuzzle.polly.core.commandline;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {
    
    private final static String FAMILY = "de.skuzzle.polly.core.commandline.Translation"; //$NON-NLS-1$
    
    public static String unknownParameter;
    public static String tooLessParameters;
    public static String invalidInt;
    public static String showingHelp;
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
