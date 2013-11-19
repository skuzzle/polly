package de.skuzzle.polly.sdk;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class Messages extends Constants {

    public final static String FAMILY = "de.skuzzle.polly.sdk.Translation"; //$NON-NLS-1$
    
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
    
    // FormalSignature
    public static String signature;
    
    // HTML
    public static String htmlTablePermDenied;
    public static String tableReload;
    public static String tablePages;
    public static String tableGotoFirst;
    public static String tableGotoFirstTitle;
    public static String tableGotoPreviousTitle;
    public static String tableGotoPageTitle;
    public static String tableGotoNextTitle;
    public static String tableGotoLast;
    public static String tableGotoLastTitle;
    public static String tablePageSize;
    public static String tableElementInfo;
    public static String tableSortBy;
    public static String tableNoFilter;
    public static String tableSelectCriteria;
    public static String tableEditTitle;
    public static String tableSubmitEditTitle;
    public static String tableFilterAll;
    public static String tableFilterTrue;
    public static String tableFilterFalse;
    
    static {
        Resources.init(FAMILY, Messages.class);
    }
}
