package polly.rx;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;


public class MyPlugin extends PollyPlugin {

    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException {
        super(myPolly);
    }

}
