package polly.tv;

import commands.TVNextCommand;
import commands.TVNowCommand;
import commands.TVProgramCommand;

import core.PatternTVProgramFormatter;
import core.TVMovieProvider;
import core.TVProgramProvider;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.PluginException;


public class MyPlugin extends PollyPlugin {
    
    public final static PatternTVProgramFormatter DEFAULT_FORMAT = 
            new PatternTVProgramFormatter("%c%: %n% (%t%, %d%)", false);

    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, DuplicatedSignatureException, PluginException {
        super(myPolly);
        
        TVProgramProvider tvProvider = new TVMovieProvider();
        this.addCommand(new TVNowCommand(myPolly, tvProvider));
        this.addCommand(new TVNextCommand(myPolly, tvProvider));
        this.addCommand(new TVProgramCommand(myPolly, tvProvider));
    }    
}
