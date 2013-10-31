package polly.mud;

import polly.mud.commands.ConnectMudCommand;
import polly.mud.commands.ForwardCommand;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;


public class MyPlugin extends PollyPlugin {
    
    public final static String MUD_PERMISSION = "polly.permission.MUD";
    
    public final static String HOST = "stv2.informatik.uni-bremen.de";
    public final static int PORT = 4444;
    private final static String NICKNAME = "Polly";
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, DuplicatedSignatureException {
        super(myPolly);
        
        MudController.create(myPolly, NICKNAME);
        
        myPolly.commands().registerCommand(new ForwardCommand(myPolly));
        myPolly.commands().registerCommand(new ConnectMudCommand(myPolly));
    }
}
