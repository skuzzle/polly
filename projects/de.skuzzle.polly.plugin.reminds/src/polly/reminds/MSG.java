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
    
    static {
        Resources.init(FAMILY, MSG.class);
    }

}
