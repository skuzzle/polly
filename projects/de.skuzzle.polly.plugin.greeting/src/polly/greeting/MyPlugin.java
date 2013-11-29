package polly.greeting;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;


public class MyPlugin extends PollyPlugin {
    
    private final DailyGreeter greeter;
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
            DuplicatedSignatureException {
        
        super(myPolly);
        this.greeter = new DailyGreeter();
        this.greeter.deploy(myPolly.irc());
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        this.greeter.undeploy(this.getMyPolly().irc());
    }
}
