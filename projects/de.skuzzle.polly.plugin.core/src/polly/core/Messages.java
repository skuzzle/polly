package polly.core;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class Messages extends Constants {
    
    private final static String FAMILY = "polly.core.Translation";
    
    // General Strings
    public static String unknownUser;
    public static String userName;
    public static String password;
    public static String wrongPassword;
    
    // AddUser Command
    public static String addUserHelp;
    public static String addUserSig0Desc;
    public static String addUserSig0UserName;
    public static String addUserSig0Password;
    public static String addUserQryOnly;
    public static String addUserSuccess;
    public static String addUserExists;
    public static String addUserInvalid;
    
    // Anyfication Command
    public static String anyficationHelp;
    public static String anyficationSig0Desc;
    public static String anyficationSig0Prefix;
    public static String anyficationSig1Desc;
    public static String anyficationSig1Prefix;
    public static String anyficationSig1Timespan;
    public static String anyficationFail;
    public static String anyficationInfo;
    
    // Auth Command
    public static String authHelp;
    public static String authSig0Desc;
    public static String authSig1Desc;
    public static String authSigPassword;
    public static String authQryWarning;
    public static String authWrongPw;
    public static String authSuccess;
    public static String authAlreadySignedOn;

    // Calendar Command
    public static String calendarHelp;
    public static String calendarSig0Desc;
    public static String calendarSig0Date;
    public static String calendarFor;
    
    // ClumBomb Command
    public static String clumBombHelp;
    public static String clumBombSig0Desc;
    public static String clumBombSig0Amount;
    
    // Define Command
    public static String defineHelp;
    public static String defineSig0Desc;
    public static String defineSig0Term;
    public static String defineMoreInfo;
    public static String defineError;
            
    // DeleteUser Command
    public static String deleteUserHelp;
    public static String deleteUserSig0Desc;
    public static String deleteUserSuccess;
    
    // Dict Command
    public static String dictHelp;
    public static String dictSig0Desc;
    public static String dictSig0Term;
    
    // Dito Command
    public static String ditoHelp;
    public static String ditoSig0Desc;
    public static String ditoNoCommand;
    
    // ExportAttributes Command
    public static String expAttributesHelp;
    public static String expAttributesSig0Desc;
    public static String expAttributesSig1Desc;
    public static String expAttributesSig1User;
    
    // Foo Command
    public static String fooHelp;
    public static String fooSig0Desc;
    public static String fooSig1Desc;
    public static String fooSig2Desc;
    public static String fooSig3Desc;
    public static String fooSigParam;
    
    // GetAttribute Command
    public static String getAttributeHelp;
    public static String getAttributeSig0Desc;
    public static String getAttributeSig0User;
    public static String getAttributeSigAttribute;
    public static String getAttributeSig1Desc;
    public static String getAttributeValue;
    public static String getAttributeUnknownAttr;
    
    // Ghost Command
    public static String ghostHelp;
    public static String ghostSig0Desc;
    public static String ghostSig0User;
    public static String ghostSig0Password;
    public static String ghostQryOnly;
    public static String ghostInvalidPw;
    public static String ghostNotLoggedIn;
    public static String ghostLoggedOut;
    
    // GooglePics Command
    public static String pixHelp;
    public static String pixSig0Desc;
    public static String pixSig0Term;
    
    // Greeting Command
    public static String greetingHelp;
    public static String greetingSig0Desc;
    public static String greetingSig0Greeting;
    public static String greetingStored;
    
    // Hop Command
    public static String hopHelp;
    public static String hopSig0Desc;
    public static String hopSig1Desc;
    public static String hopSig1Channel;
    public static String hopSpecifyChannel;
    public static String hopPartMessage;
    
    // Info Command
    public static String infoHelp;
    public static String infoSig0Desc;
    public static String infoSig0Command;
    public static String infoSig1Desc;
    public static String infoSig1User;
    public static String infoCommandInfo;
    public static String infoUnknownCommand;
    public static String infoUnknownUser;
    public static String infoUserInfo;
    
    // IsDown Command
    public static String isDownHelp;
    public static String isDownSig0Desc;
    public static String isDownSig0Url;
    public static String isDownSig1Desc;
    public static String isDownSig1Url;
    public static String isDownSig1Timeout;
    public static String isDownReachable;
    public static String isDownInvalidUrl;
    public static String isDownUnknownHost;
    public static String isDownNotReachable;
    
    // Join Command
    public static String joinHelp;
    public static String joinSig0Desc;
    public static String joinSig0Channel;
    public static String joinSig1Desc;
    public static String joinSig1Channels;
    public static String joinSig2Desc;
    public static String joinSig2Channel;
    public static String joinSig2Password;
    
    // Kick Command
    public static String kickHelp;
    public static String kickSig0Desc;
    public static String kickSig0User;
    public static String kickSig1Desc;
    public static String kickSig1User;
    public static String kickSig1Reason;
    public static String kickSig2Desc;
    public static String kickSig2Channel;
    public static String kickSig2User;
    public static String kickSig3Desc;
    public static String kickSig3Channel;
    public static String kickSig3User;
    public static String kickSig3Reason;
    public static String kickSig4Desc;
    public static String kickSig4Users;
    public static String kickSig5Desc;
    public static String kickSig5Users;
    public static String kickSig5Channel;
    
    
    // ListAttributes Command
    public static String listAttributesHelp;
    public static String listAttributesSig0Desc;
    
    // Lmgtfy Command
    public static String lmgtfyHelp;
    public static String lmgtfySig0Desc;
    public static String lmgtfySig0Term;
    
    // Part Command
    public static String partHelp;
    public static String partSig0Desc;
    public static String partSig1Desc;
    public static String partSig1Channel;
    public static String partSpecifyChannel;
    public static String partMessage;
    
    // Quit Command
    public static String quitHelp;
    public static String quitSig0Desc;
    public static String quitSig1Desc;
    public static String quitSig1QuitMsg;
    public static String quitDefaultQuitMsg;
    public static String quitConfirm;
    public static String quitTimeout;
    
    // Raw Command
    public static String rawHelp;
    public static String rawSig0Desc;
    public static String rawSig0Cmd;
    
    // ReAuth Command
    public static String reAuthHelp;
    public static String reAuthSig0Desc;
    public static String reAuthSig1Desc;
    public static String reAuthSig1Join;
    
    // Register Command
    public static String registerHelp;
    public static String registerSig0Desc;
    public static String registerSig0Password;
    public static String registerQryWarning;
    public static String registerAlreadySignedOn;
    public static String registerSuccess;
    public static String registerAlreadyExists;
    public static String registerInvalidName;
    
    
    static {
        Resources.init(FAMILY, Messages.class);
    }
}
