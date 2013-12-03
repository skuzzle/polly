package polly.dyndns;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {

    public final static String FAMILY = "polly.dyndns.Translation"; //$NON-NLS-1$

    public static String category;
    public static String hosterDescription;
    public static String hosterName;
    public static String htmlAddHoster;
    public static String htmlAddAccount;
    public static String htmlAllHosters;
    public static String htmlHosterName;
    public static String htmlHost;
    public static String htmlUserName;
    public static String htmlPassword;
    public static String htmlUpdateUrl;
    public static String htmlUrlDescription;
    public static String htmlSubmit;
    public static String htmlIpStatus;
    public static String htmlAccountStatus;
    public static String htmlAccountTime;
    public static String htmlDelete;
    public static String htmlRefresh;
    public static String htmlNoHostersConfigured;
    public static String htmlDomainName;
    public static String htmlHoster;
    public static String htmlAccounts;
    public static String none;
    public static String updateError;
    public static String unknownAccountId;
    public static String unknownHosterId;

    
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
