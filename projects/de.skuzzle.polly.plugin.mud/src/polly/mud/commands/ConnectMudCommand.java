package polly.mud.commands;

import java.io.IOException;

import polly.mud.MudController;
import polly.mud.MyPlugin;
import polly.mud.connection.ConnectionListener;
import polly.mud.connection.MudEvent;
import polly.mud.connection.MudMessageEvent;
import polly.mud.connection.MudTCPConnection;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public class ConnectMudCommand extends Command {

    private MudTCPConnection connection;
    
    public ConnectMudCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "connectMud");
        this.createSignature("", MyPlugin.MUD_PERMISSION);
    }
    
    
    
    @Override
    protected synchronized boolean executeOnBoth(User executer, String channel, 
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            
            if (this.connection == null) {
                try {
                    this.connection = MudTCPConnection.connect(
                            MyPlugin.HOST, MyPlugin.PORT, 
                            MudController.getInstance());
                    this.reply(channel, "Verbindung hergestellt");
                    this.connection.addConnectionListener(new ConnectionListener() {
                        
                        @Override
                        public void received(MudMessageEvent e) {
                        }
                        
                        
                        
                        @Override
                        public void disconnected(MudEvent e) {
                            connection = null;
                        }
                        
                        
                        
                        @Override
                        public void connected(MudEvent e) {
                        }
                    });
                } catch (IOException e) {
                    throw new CommandException(e);
                }
            } else {
                try {
                    this.connection.close();
                    this.reply(channel, "Mud Verbindung geschlossen");
                } catch (IOException e) {
                    throw new CommandException(e);
                } finally {
                    this.connection = null;
                }
            }
            
        }
        
        return super.executeOnBoth(executer, channel, signature);
    }

}
