package polly.core;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.PString;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {
    
    private final static String FAMILY = "polly.core.Translation"; //$NON-NLS-1$
    
    // General Strings
    public static PString unknownUser;
    public static PString userName;
    public static PString password;
    public static PString wrongPassword;
    
    // NewYearCountdown
    public static PString newYearHappyNewYear;
    
    // Attributes
    public static PString attributeGreetingDescription;
    
    // AddUser Command
    public static PString addUserHelp;
    public static PString addUserSig0Desc;
    public static PString addUserSig0UserName;
    public static PString addUserSig0Password;
    public static PString addUserQryOnly;
    public static PString addUserSuccess;
    public static PString addUserExists;
    public static PString addUserInvalid;
    
    // Anyfication Command
    public static PString anyficationHelp;
    public static PString anyficationSig0Desc;
    public static PString anyficationSig0Prefix;
    public static PString anyficationSig1Desc;
    public static PString anyficationSig1Prefix;
    public static PString anyficationSig1Timespan;
    public static PString anyficationFail;
    public static PString anyficationInfo;
    
    // Auth Command
    public static PString authHelp;
    public static PString authSig0Desc;
    public static PString authSig1Desc;
    public static PString authSigPassword;
    public static PString authQryWarning;
    public static PString authWrongPw;
    public static PString authSuccess;
    public static PString authAlreadySignedOn;

    // Calendar Command
    public static PString calendarHelp;
    public static PString calendarSig0Desc;
    public static PString calendarSig0Date;
    public static PString calendarFor;
    
    // ClumBomb Command
    public static PString clumBombHelp;
    public static PString clumBombSig0Desc;
    public static PString clumBombSig0Amount;
    
    // Define Command
    public static PString defineHelp;
    public static PString defineSig0Desc;
    public static PString defineSig0Term;
    public static PString defineMoreInfo;
    public static PString defineError;
            
    // DeleteUser Command
    public static PString deleteUserHelp;
    public static PString deleteUserSig0Desc;
    public static PString deleteUserSuccess;
    
    // Dict Command
    public static PString dictHelp;
    public static PString dictSig0Desc;
    public static PString dictSig0Term;
    
    // Dito Command
    public static PString ditoHelp;
    public static PString ditoSig0Desc;
    public static PString ditoNoCommand;
    
    // ExportAttributes Command
    public static PString expAttributesHelp;
    public static PString expAttributesSig0Desc;
    public static PString expAttributesSig1Desc;
    public static PString expAttributesSig1User;
    
    // Foo Command
    public static PString fooHelp;
    public static PString fooSig0Desc;
    public static PString fooSig1Desc;
    public static PString fooSig2Desc;
    public static PString fooSig3Desc;
    public static PString fooSigParam;
    
    // GetAttribute Command
    public static PString getAttributeHelp;
    public static PString getAttributeSig0Desc;
    public static PString getAttributeSig0User;
    public static PString getAttributeSigAttribute;
    public static PString getAttributeSig1Desc;
    public static PString getAttributeValue;
    public static PString getAttributeUnknownAttr;
    
    // Ghost Command
    public static PString ghostHelp;
    public static PString ghostSig0Desc;
    public static PString ghostSig0User;
    public static PString ghostSig0Password;
    public static PString ghostQryOnly;
    public static PString ghostInvalidPw;
    public static PString ghostNotLoggedIn;
    public static PString ghostLoggedOut;
    
    // GooglePics Command
    public static PString pixHelp;
    public static PString pixSig0Desc;
    public static PString pixSig0Term;
    
    // Greeting Command
    public static PString greetingHelp;
    public static PString greetingSig0Desc;
    public static PString greetingSig0Greeting;
    public static PString greetingStored;
    
    // Hop Command
    public static PString hopHelp;
    public static PString hopSig0Desc;
    public static PString hopSig1Desc;
    public static PString hopSig1Channel;
    public static PString hopSpecifyChannel;
    public static PString hopPartMessage;
    
    // Info Command
    public static PString infoHelp;
    public static PString infoSig0Desc;
    public static PString infoSig0Command;
    public static PString infoSig1Desc;
    public static PString infoSig1User;
    public static PString infoCommandInfo;
    public static PString infoUnknownCommand;
    public static PString infoUnknownUser;
    public static PString infoUserInfo;
    
    // IsDown Command
    public static PString isDownHelp;
    public static PString isDownSig0Desc;
    public static PString isDownSig0Url;
    public static PString isDownSig1Desc;
    public static PString isDownSig1Url;
    public static PString isDownSig1Timeout;
    public static PString isDownReachable;
    public static PString isDownInvalidUrl;
    public static PString isDownUnknownHost;
    public static PString isDownNotReachable;
    
    // Join Command
    public static PString joinHelp;
    public static PString joinSig0Desc;
    public static PString joinSig0Channel;
    public static PString joinSig1Desc;
    public static PString joinSig1Channels;
    public static PString joinSig2Desc;
    public static PString joinSig2Channel;
    public static PString joinSig2Password;
    
    // Kick Command
    public static PString kickHelp;
    public static PString kickSig0Desc;
    public static PString kickSig0User;
    public static PString kickSig1Desc;
    public static PString kickSig1User;
    public static PString kickSig1Reason;
    public static PString kickSig2Desc;
    public static PString kickSig2Channel;
    public static PString kickSig2User;
    public static PString kickSig3Desc;
    public static PString kickSig3Channel;
    public static PString kickSig3User;
    public static PString kickSig3Reason;
    public static PString kickSig4Desc;
    public static PString kickSig4Users;
    public static PString kickSig5Desc;
    public static PString kickSig5Users;
    public static PString kickSig5Channel;
    
    
    // ListAttributes Command
    public static PString listAttributesHelp;
    public static PString listAttributesSig0Desc;
    
    // Lmgtfy Command
    public static PString lmgtfyHelp;
    public static PString lmgtfySig0Desc;
    public static PString lmgtfySig0Term;
    
    // Part Command
    public static PString partHelp;
    public static PString partSig0Desc;
    public static PString partSig1Desc;
    public static PString partSig1Channel;
    public static PString partSpecifyChannel;
    public static PString partMessage;
    
    // Quit Command
    public static PString quitHelp;
    public static PString quitSig0Desc;
    public static PString quitSig1Desc;
    public static PString quitSig1QuitMsg;
    public static PString quitDefaultQuitMsg;
    public static PString quitConfirm;
    public static PString quitTimeout;
    
    // Raw Command
    public static PString rawHelp;
    public static PString rawSig0Desc;
    public static PString rawSig0Cmd;
    
    // ReAuth Command
    public static PString reAuthHelp;
    public static PString reAuthSig0Desc;
    public static PString reAuthSig1Desc;
    public static PString reAuthSig1Join;
    
    // Register Command
    public static PString registerHelp;
    public static PString registerSig0Desc;
    public static PString registerSig0Password;
    public static PString registerQryWarning;
    public static PString registerAlreadySignedOn;
    public static PString registerSuccess;
    public static PString registerAlreadyExists;
    public static PString registerInvalidName;
    
    // Restart Command
    public static PString restartHelp;
    public static PString restartSig0Desc;
    public static PString restartSig1Desc;
    public static PString restartSig1Params;
    
    // SetAttribute Command
    public static PString setAttributeHelp;
    public static PString setAttributeSig0Desc;
    public static PString setAttributeSig0User;
    public static PString setAttributeSig0Attr;
    public static PString setAttributeSig0Value;
    public static PString setAttributeSig1Desc;
    public static PString setAttributeSig1Attr;
    public static PString setAttributSig1Value;
    public static PString setAttributeUnknownUser;
    public static PString setAttributeUnknownAttr;
    public static PString setAttributeSuccess;
    
    // SetMyPassword Command
    public static PString setMyPwHelp;
    public static PString setMyPwSig0Desc;
    public static PString setMyPwSig0CurrentPw;
    public static PString setMyPwSig0NewPw;
    public static PString setMyPwQryWarning;
    public static PString setMyPwMismatch;
    
    // SetPassword Command
    public static PString setPasswordHelp;
    public static PString setPasswordSig0Desc;
    public static PString setPasswordSig0User;
    public static PString setPasswordSig0Password;
    public static PString setPasswordQryWarnning;
    public static PString setPasswordUnknownUser;
    public static PString setPasswordSuccess;
    
    // ShowCmds Command
    public static PString showCmdsHelp;
    public static PString showCmdsSig0Desc;
    public static PString showCmdsAvailable;
    
    // SignOff Command
    public static PString signOffHelp;
    public static PString signOffSig0Desc;
    public static PString signOffSuccess;
    
    // Talk Command
    public static PString talkHelp;
    public static PString talkSig0Desc;
    public static PString talkSig0Msg;
    public static PString talkSig1Desc;
    public static PString talkSig1Channel;
    public static PString talkSig1Msg;
    
    // Uptime Command
    public static PString uptimeHelp;
    public static PString uptimeSig0Desc;
    public static PString uptimeSig1Desc;
    public static PString uptimeSig1Nick;
    public static PString uptimeOnlineSince;
    public static PString uptimeOffline;
    
    // Users Command
    public static PString usersHelp;
    public static PString usersSig0Desc;
    public static PString usersSig1Desc;
    public static PString usersSig1Pattern;
    public static PString usersSig2Desc;
    public static PString usersSig2Pattern;
    public static PString usersSig2LoggedInOnly;
    public static PString usersNoUsers;
    
    // Var Command
    public static PString varHelp;
    public static PString varSig0Desc;
    public static PString varSig1Desc;
    public static PString varSig1Namespace;
    public static PString varSig2Desc;
    public static PString varSig2User;
    public static PString varDeclarations;
    
    // Version Command
    public static PString versionHelp;
    public static PString versionSig0Desc;
    public static PString versionPollyVersion;
    
    // Webinterface Command
    public static PString webHelp;
    public static PString webSig0Desc;
    public static PString webSig1Desc;
    public static PString webSig1OnOff;
    public static PString webShowUrl;
    public static PString webOffline;
    public static PString webTurnedOff;
    public static PString webTurnedOn;
    
    // Wiki Command
    public static PString wikiHelp;
    public static PString wikiSig0Desc;
    public static PString wikiSig0Term;
    
    // AssignPermission Command
    public static PString assignPermHelp;
    public static PString assignPermSig0Desc;
    public static PString assignPermSig0Role;
    public static PString assignPermSig0Perm;
    public static PString assignPermSuccess;
    
    // AssignRole Command
    public static PString assignRoleHelp;
    public static PString assignRoleSig0Desc;
    public static PString assignRoleSig0User;
    public static PString assignRoleSig0Role;
    public static PString assignRoleUnknownUser;
    public static PString assignRoleSuccess;
    
    // CreateRole Command
    public static PString createRoleHelp;
    public static PString createRoleSig0Desc;
    public static PString createRoleSig0Name;
    public static PString createRoleSig1Desc;
    public static PString createRoleSig1Base;
    public static PString createRoleSig1Name;
    public static PString createRoleSuccess;
    
    // DeleteRole Command
    public static PString deleteRoleHelp;
    public static PString deleteRoleSig0Desc;
    public static PString deleteRoleSig0Name;
    public static PString deleteRoleSuccess;
    
    // ListPermissions Command
    public static PString listPermHelp;
    public static PString listPermSig0Desc;
    public static PString listPermSig0Name;
    public static PString listPermUnknownRole;
    
    // ListRoles Command
    public static PString listRolesHelp;
    public static PString listRolesSig0Desc;
    public static PString listRolesSig0User;
    public static PString listRolesSig1Desc;
    public static PString listRolesUnknownUser;
    
    // RemovePermission Command
    public static PString removePermHelp;
    public static PString removePermSig0Desc;
    public static PString removePermSig0Role;
    public static PString removePermSig0Perm;
    public static PString removePermSuccess;
    
    // RemoveRole Command
    public static PString removeRoleHelp;
    public static PString removeRoleSig0Desc;
    public static PString removeRoleSig0User;
    public static PString removeRoleSig0Role;
    public static PString removeRoleUnknownUser;
    public static PString removeRoleSuccess;
    
    static {
        Resources.initPString(FAMILY, MSG.class);
    }
}
