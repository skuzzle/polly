package polly.logging;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public final class MSG extends Constants {

    public final static String FAMILY = "polly.logging.Translation"; //$NON-NLS-1$
    
    // General Strings
    public static String userName;
    
    // ChannelLog Command
    public static String logHelp;
    public static String logSig0Desc;
    public static String logSig0Channel;
    public static String logSig1Desc;
    public static String logSig1Channel;
    public static String logSig1Pattern;
    public static String logSig2Desc;
    public static String logSig2Channel;
    public static String logSig2Pattern;
    public static String logSig2Date;
    public static String logSig3Desc;
    public static String logSig3Channel;
    public static String logSig3Pattern;
    public static String logSig3From;
    public static String logSig3To;
    public static String logSig4Desc;
    public static String logSig4Pattern;
    public static String logSig5Desc;
    
    // Replay Command
    public static String replayHelp;
    public static String replaySig0Desc;
    
    // Seen Command
    public static String seenHelp;
    public static String seenSig0Desc;
    public static String seendSig0User;
    public static String seenHidden;
    
    // UserLog Command
    public static String userLogHelp;
    public static String userLogSig0Desc;
    public static String userLogSig0User;
    public static String userLogSig1Desc;
    public static String userLogSig1User;
    public static String userLogSig1Pattern; 
    public static String userLogSig2Desc;
    public static String userLogSig2User;
    public static String userLogSig2Pattern;
    public static String userLogSig2Limit;
    public static String userLogSig3Desc;
    public static String userLogSig3User;
    public static String userLogSig3Pattern;
    public static String userLogSig3Date;
    public static String userLogSig4Desc;
    public static String userLogSig4User;
    public static String userLogSig4Pattern;
    public static String userLogSig4From;
    public static String userLogSig4To;
    
    // DefaultLogFormat
    public static String logFormatNoData;
    
    // ForwardHighlightHandler
    public static String forwardSubject;
    public static String forwardMessage;
    
    // PasteServiceLogOutput
    public static String pasteOutputFail;
    public static String pasteOutputResult;
    
    // LogEntryTableModel
    public static String logTableDateCol;
    public static String logTableChannelCol;
    public static String logTableUserCol;
    public static String logTableTypeCol;
    public static String logTableMessageCol;
    public static String logEntryTypeJoin;
    public static String logEntryTypeNickchange;
    public static String logEntryTypeMessage;
    public static String logEntryTypePart;
    public static String logEntryTypeQuit;
    
    // Logging Controller
    public static String httpLoggingCategory;
    public static String httpSearchLog;
    public static String httpSearchLogsDesc;
    public static String httpReplay;
    public static String httpReplayDesc;
    
    // Plugin
    public static String forwardHLDesc;
    public static String loggingAttributeCategory;
    
    // HTML Pages
    public static String htmlSearchLogsCaption;
    public static String htmlReplayCaption;
            
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
