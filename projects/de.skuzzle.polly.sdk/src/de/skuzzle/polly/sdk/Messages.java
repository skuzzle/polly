package de.skuzzle.polly.sdk;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.PString;
import de.skuzzle.polly.sdk.resources.Resources;


public class Messages extends Constants {

    public final static String FAMILY = "de.skuzzle.polly.sdk.Translation"; //$NON-NLS-1$
    
    // Command
    public static PString commandNoDescription;
    public static PString commandSignatures;
    public static PString commandNoSignature;
    public static PString commandNoSignatureId;
    public static PString commandSample;
    
    // Delayed Command
    public static PString delayedCommandCantExecute;
    
    // Types
    public static PString typesStringSample;
    
    // FormalSignature
    public static PString signature;
    
    // HTML
    public static PString htmlTablePermDenied;
    public static PString tableReload;
    public static PString tablePages;
    public static PString tableGotoFirst;
    public static PString tableGotoFirstTitle;
    public static PString tableGotoPreviousTitle;
    public static PString tableGotoPageTitle;
    public static PString tableGotoNextTitle;
    public static PString tableGotoLast;
    public static PString tableGotoLastTitle;
    public static PString tablePageSize;
    public static PString tableElementInfo;
    public static PString tableSortBy;
    public static PString tableNoFilter;
    public static PString tableSelectCriteria;
    public static PString tableEditTitle;
    public static PString tableSubmitEditTitle;
    public static PString tableFilterAll;
    public static PString tableFilterTrue;
    public static PString tableFilterFalse;
    
    static {
        Resources.initPString(FAMILY, Messages.class);
    }
}
