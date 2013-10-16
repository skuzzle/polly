package commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polly.core.MSG;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class ShowCommandsCommand extends Command {

    public ShowCommandsCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "cmds"); //$NON-NLS-1$
        this.createSignature(MSG.showCmdsSig0Desc.s);
        this.setHelpText(MSG.showCmdsHelp.s);
    }
    
    
    
    private boolean canExecute(User user, Command cmd) {
        return this.getMyPolly().roles().canAccess(user, cmd);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        StringBuilder b = new StringBuilder();
        List<Command> cmds = new ArrayList<Command>(
                this.getMyPolly().commands().getRegisteredCommands());
        Collections.sort(cmds);
        List<Command> output = new ArrayList<Command>(20);
        
        for (Command cmd : cmds) {
            if (this.canExecute(executer, cmd)) {
                output.add(cmd);
            }
        }
        
        Iterator<Command> it = output.iterator();
        while (it.hasNext()) {
            Command cmd = it.next();
            b.append(cmd.toString());
            if (it.hasNext()) {
                b.append(", "); //$NON-NLS-1$
            }
        }
        
        this.reply(channel, MSG.showCmdsAvailable.s(b.toString()));
        return false;
    }

}
