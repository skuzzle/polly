package polly.greeting;

import de.skuzzle.polly.sdk.resources.Constants;
import de.skuzzle.polly.sdk.resources.Resources;


public class MSG extends Constants {

    private final static String FAMILY = "polly.greeting.Translation"; //$NON-NLS-1$

    public static String greeterGreetings;
    public static String greeterQuestions;
    
    static {
        Resources.init(FAMILY, MSG.class);
    }
}
