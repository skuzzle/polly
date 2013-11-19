package de.skuzzle.polly.core;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {
    
    private final static String FAMILY = "de.skuzzle.polly.core.Translation"; //$NON-NLS-1$
    
    public static String alreadyRunning;
    public static String configError;
    public static String configErrorReason1;
    public static String configErrorReason2;
    public static String configErrorReason3;
    public static String paramCaption;
    public static String paramHeader;
    public static String paramLog;
    public static String paramNick;
    public static String paramIdent;
    public static String paramServer;
    public static String paramPort;
    public static String paramJoin;
    public static String paramHelp;
    public static String paramClose;
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
