package polly.annoyingPeople;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;


public class MyPlugin extends PollyPlugin {
    
    public final static String PERMISSION_ADD_ANNOYING_PERSON = "polly.permission.ADD_ANNOYING_PERSON";
    
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException {
        super(myPolly);
    }


}
