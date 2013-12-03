package polly.dyndns;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {

    public final static String FAMILY = "polly.dyndns.Translation"; //$NON-NLS-1$

    public static String category;
    public static String hosterDescription;
    public static String hosterName;
    public static String htmlAddHoster;
    public static String htmlAllHosters;
    public static String htmlHosterName;
    public static String htmlHost;
    public static String htmlUserName;
    public static String htmlPassword;
    public static String htmlUpdateUrl;
    public static String htmlUrlDescription;
    public static String htmlSubmit;
    public static String htmlIpStatus;
    public static String htmlHosterStatus;
    public static String htmlHosterTime;
    public static String htmlDelete;
    public static String htmlRefresh;

    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
