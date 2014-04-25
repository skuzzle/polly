package polly.core;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {
    
    private final static String FAMILY = "polly.core.Translation"; //$NON-NLS-1$
    
    // General Strings
    public static String unknownUser;
    public static String userName;
    public static String password;
    public static String wrongPassword;
    
    // NewYearCountdown
    public static String newYearHappyNewYear;
    
    // Attributes
    public static String attributeGreetingDescription;
    
    // AddUser Command
    public static String addUserHelp;
    public static String addUserSig0Desc;
    public static String addUserSig0UserName;
    public static String addUserSig0Password;
    public static String addUserQryOnly;
    public static String addUserSuccess;
    public static String addUserExists;
    public static String addUserInvalid;
    
    // AmazonCommand
    public static String amazonHelp;
    public static String amazoneSig0Desc;
    public static String amazonSig0Term;
    
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
    
    // Restart Command
    public static String restartHelp;
    public static String restartSig0Desc;
    public static String restartSig1Desc;
    public static String restartSig1Params;
    
    // SetAttribute Command
    public static String setAttributeHelp;
    public static String setAttributeSig0Desc;
    public static String setAttributeSig0User;
    public static String setAttributeSig0Attr;
    public static String setAttributeSig0Value;
    public static String setAttributeSig1Desc;
    public static String setAttributeSig1Attr;
    public static String setAttributSig1Value;
    public static String setAttributeUnknownUser;
    public static String setAttributeUnknownAttr;
    public static String setAttributeSuccess;
    
    // SetMyPassword Command
    public static String setMyPwHelp;
    public static String setMyPwSig0Desc;
    public static String setMyPwSig0CurrentPw;
    public static String setMyPwSig0NewPw;
    public static String setMyPwQryWarning;
    public static String setMyPwMismatch;
    
    // SetPassword Command
    public static String setPasswordHelp;
    public static String setPasswordSig0Desc;
    public static String setPasswordSig0User;
    public static String setPasswordSig0Password;
    public static String setPasswordQryWarnning;
    public static String setPasswordUnknownUser;
    public static String setPasswordSuccess;
    
    // ShowCmds Command
    public static String showCmdsHelp;
    public static String showCmdsSig0Desc;
    public static String showCmdsAvailable;
    
    // SignOff Command
    public static String signOffHelp;
    public static String signOffSig0Desc;
    public static String signOffSuccess;
    
    // Talk Command
    public static String talkHelp;
    public static String talkSig0Desc;
    public static String talkSig0Msg;
    public static String talkSig1Desc;
    public static String talkSig1Channel;
    public static String talkSig1Msg;
    public static String talkSig2Desc;
    public static String talkSig2User;
    
    // Uptime Command
    public static String uptimeHelp;
    public static String uptimeSig0Desc;
    public static String uptimeSig1Desc;
    public static String uptimeSig1Nick;
    public static String uptimeOnlineSince;
    public static String uptimeOffline;
    
    // Users Command
    public static String usersHelp;
    public static String usersSig0Desc;
    public static String usersSig1Desc;
    public static String usersSig1Pattern;
    public static String usersSig2Desc;
    public static String usersSig2Pattern;
    public static String usersSig2LoggedInOnly;
    public static String usersNoUsers;
    
    // Var Command
    public static String varHelp;
    public static String varSig0Desc;
    public static String varSig1Desc;
    public static String varSig1Namespace;
    public static String varSig2Desc;
    public static String varSig2User;
    public static String varDeclarations;
    
    // Version Command
    public static String versionHelp;
    public static String versionSig0Desc;
    public static String versionPollyVersion;
    
    // Webinterface Command
    public static String webHelp;
    public static String webSig0Desc;
    public static String webSig1Desc;
    public static String webSig1OnOff;
    public static String webShowUrl;
    public static String webOffline;
    public static String webTurnedOff;
    public static String webTurnedOn;
    
    // Wiki Command
    public static String wikiHelp;
    public static String wikiSig0Desc;
    public static String wikiSig0Term;
    
    // AssignPermission Command
    public static String assignPermHelp;
    public static String assignPermSig0Desc;
    public static String assignPermSig0Role;
    public static String assignPermSig0Perm;
    public static String assignPermSuccess;
    
    // AssignRole Command
    public static String assignRoleHelp;
    public static String assignRoleSig0Desc;
    public static String assignRoleSig0User;
    public static String assignRoleSig0Role;
    public static String assignRoleUnknownUser;
    public static String assignRoleSuccess;
    
    // CreateRole Command
    public static String createRoleHelp;
    public static String createRoleSig0Desc;
    public static String createRoleSig0Name;
    public static String createRoleSig1Desc;
    public static String createRoleSig1Base;
    public static String createRoleSig1Name;
    public static String createRoleSuccess;
    
    // DeleteRole Command
    public static String deleteRoleHelp;
    public static String deleteRoleSig0Desc;
    public static String deleteRoleSig0Name;
    public static String deleteRoleSuccess;
    
    // ListPermissions Command
    public static String listPermHelp;
    public static String listPermSig0Desc;
    public static String listPermSig0Name;
    public static String listPermUnknownRole;
    
    // ListRoles Command
    public static String listRolesHelp;
    public static String listRolesSig0Desc;
    public static String listRolesSig0User;
    public static String listRolesSig1Desc;
    public static String listRolesUnknownUser;
    
    // RemovePermission Command
    public static String removePermHelp;
    public static String removePermSig0Desc;
    public static String removePermSig0Role;
    public static String removePermSig0Perm;
    public static String removePermSuccess;
    
    // RemoveRole Command
    public static String removeRoleHelp;
    public static String removeRoleSig0Desc;
    public static String removeRoleSig0User;
    public static String removeRoleSig0Role;
    public static String removeRoleUnknownUser;
    public static String removeRoleSuccess;
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
