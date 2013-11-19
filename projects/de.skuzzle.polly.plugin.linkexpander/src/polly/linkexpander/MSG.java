package polly.linkexpander;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {

    private final static String FAMILY = "polly.linkexpander.Translation"; //$NON-NLS-1$
    
    // LinkGrabber Command
    public static String linkGrabberHelp;
    public static String linkGrabberSig0Desc;
    public static String linkGrabberSig0Status;
    public static String linkGrabberOn;
    public static String linkGrabberOff;
    public static String linkGrabberStatus;
    
    // PhpBBLinkGrabber
    public static String phpBBTopic;
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
