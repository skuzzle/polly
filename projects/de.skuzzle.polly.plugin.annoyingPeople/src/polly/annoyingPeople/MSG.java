package polly.annoyingPeople;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {
    
    private final static String FAMILY = "polly.annoyingPeople.Translation"; //$NON-NLS-1$

    public static String aktienNames;
    public static String ressNames;
    public static String quadNames; 
    public static String askForRessPrice;
    public static String askForCourse;
    public static String askIfDown;
    public static String askForCode;
    public static String askForDirection;
    public static String askForKonstru;
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
