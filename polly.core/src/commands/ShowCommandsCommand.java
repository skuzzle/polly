package commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.Types.NumberType;



public class ShowCommandsCommand extends Command {

    public ShowCommandsCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "cmds");
        this.createSignature("Zeigt alle für dich ausführbaren Befehle an.");
        this.createSignature("Zeigt alle Befehle für das angegebene User-Level an.", 
                new NumberType());
        this.setHelpText("Listet die verfügbaren Befehle auf.");
    }
    
    
    
    private boolean canExecute(User user, int userLevel, Command cmd) {
        boolean userOnline = this.getMyPolly().users().isSignedOn(user);
        boolean result = cmd.getUserLevel() <= userLevel;
        
        result = userOnline ? result : result && !cmd.isRegisteredOnly();
        return result;
    }
    
    
    @Override
    public void renewConstants() {
        this.registerConstant("ADMIN", new NumberType(UserManager.ADMIN));
        this.registerConstant("MEMBER", new NumberType(UserManager.MEMBER));
        this.registerConstant("REG", new NumberType(UserManager.REGISTERED));
        this.registerConstant("UNKNOWN", new NumberType(UserManager.UNKNOWN));
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {

        int level = executer.getUserLevel();
        if (this.match(signature, 1)) {
            level = (int) signature.getNumberValue(0);
        }
        
        StringBuilder b = new StringBuilder();
        List<Command> cmds = new ArrayList<Command>(
                this.getMyPolly().commands().getRegisteredCommands());
        Collections.sort(cmds);
        List<Command> output = new ArrayList<Command>(20);
        
        for (Command cmd : cmds) {
            if (this.canExecute(executer, level, cmd)) {
                output.add(cmd);
            }
        }
        
        Iterator<Command> it = output.iterator();
        while (it.hasNext()) {
            Command cmd = it.next();
            b.append(cmd.toString());
            if (it.hasNext()) {
                b.append(", ");
            }
        }
        
        this.reply(channel, "Verfügbare Befehle: " + b.toString());
        return false;
    }

}
