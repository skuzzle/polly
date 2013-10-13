package polly.reminds;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {
    
    private final static String FAMILY = "polly.reminds.Translation"; //$NON-NLS-1$
    
    // MyPlugin
    public static String remindFormatValue;
    public static String remindCategory;
    public static String remindFormatDesc;
    public static String remindSnoozeDesc;
    public static String remindDefaultMsgDesc;
    public static String remindEmailDesc;
    public static String remindLeaveAsMailDesc;
    public static String remindIdleTimeDesc;
    public static String remindTrackNickchangeDesc;
    public static String remindDoubleDeliveryDesc;
    public static String remindDefaultRemindTimeDesc;
    public static String remindAutoSnoozeDesc;
    public static String remindAutoSnoozeIndiDesc;
    public static String remindUseSnoozeTimeDesc;
    
    // AbstractRemindCommand
    public static String abstractRemindCommandRemindFormat;
    public static String abstractRemindCommandMessageFormat;
    
    // DeleteRemindCommand
    public static String delRemindHelp;
    public static String delRemindSig0Desc;
    public static String delRemindSig0Id;
    public static String delRemindSig1Desc;
    public static String delRemindSuccess;
    
    // LeaveCommand
    public static String leaveHelp;
    public static String leaveSig0Desc;
    public static String leaveSig0User;
    public static String leaveSig0Channel;
    public static String leaveSig0Message;
    public static String leaveSig1Desc;
    public static String leaveSig1Users;
    public static String leaveSig1Channel;
    public static String leaveSig1Message;
    public static String leaveSig2Desc;
    public static String leaveSig2User;
    public static String leaveSig2Message;
    public static String leaveMultipleSuccess;
    
    // MailRemindCommand
    public static String mremindHelp;
    public static String mremindSig0Desc;
    public static String mremindSig0User;
    public static String mremindSig0Date;
    public static String mremindSig0Message;
    public static String mremindSig1Desc;
    public static String mremindSig1Date;
    public static String mremindSig1Message;
    public static String mremindSig2Desc;
    public static String mremindSig2Date;
    public static String mremindSuccess;
    public static String mremindUnknownUser;
    
    // ModRemindCommand
    public static String modRemindHelp;
    public static String modRemindSig0Desc;
    public static String modRemindSig0Id;
    public static String modRemindSig0NewTime;
    public static String modRemindSig1Desc;
    public static String modRemindSig1Id;
    public static String modRemindSig1Message;
    public static String modRemindSig2Desc;
    public static String modRemindSig2Id;
    public static String modRemindSig2Message;
    public static String modRemindSig2NewTime;
    public static String modRemindSig3Desc;
    public static String modRemindSig3NewTime;
    public static String modRemindSig4Desc;
    public static String modRemindSig4Message;
    public static String modRemindSig5Desc;
    public static String modRemindSig5Message;
    public static String modRemindSig5NewTime;
    public static String modRemindNoRemind;
    public static String modRemindSuccess;
    
    //  MyRemindCommand
    public static String myRemindHelp;
    public static String myRemindSig0Desc;
    public static String myRemindNoRemind;
    public static String myRemindFormatMessage;
    public static String myRemindFormatRemind;
    public static String myRemindFormatRemindMail;
    
    // OnReturnCommand
    public static String onReturnHelp;
    public static String onReturnSig0Desc;
    public static String onReturnSig0User;
    public static String onReturnSig0Message;
    
    // RemindCommand
    public static String remindCmdHelp;
    public static String remindCmdSig0Desc;
    public static String remindCmdSig0User;
    public static String remindCmdSig0Channel;
    public static String remindCmdSig0Time;
    public static String remindCmdSig0Message;
    public static String remindCmdSig1Desc;
    public static String remindCmdSig1Users;
    public static String remindCmdSig1Channel;
    public static String remindCmdSig1Time;
    public static String remindCmdSig1Message;
    public static String remindCmdSig2Desc;
    public static String remindCmdSig2Time;
    public static String remindCmdSig2Message;
    public static String remindCmdSig3Desc;
    public static String remindCmdSig3User;
    public static String remindCmdSig3Time;
    public static String remindCmdSig3Message;
    public static String remindCmdSig4Desc;
    public static String remindCmdSig4Time;
    public static String remindCmdSig5Desc;
    public static String remindCmdMultipleUsersSuccess;
    
    
    static {
        Resources.init(FAMILY, MSG.class);
    }

}
