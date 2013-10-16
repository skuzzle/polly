package commands;


import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.Conversation;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


/**
 * 
 * @author Simon
 * @version 27.07.2011 3851c1b
 */
public class QuitCommand extends Command {

    private final static String[] answers = {
        "ja",  //$NON-NLS-1$
        "yo",  //$NON-NLS-1$
        "jup",  //$NON-NLS-1$
        "yes",  //$NON-NLS-1$
        "jo",  //$NON-NLS-1$
        "ack" //$NON-NLS-1$
    };
    
    public QuitCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "flyaway"); //$NON-NLS-1$
        this.createSignature(MSG.quitSig0Desc.s, MyPlugin.QUIT_PERMISSION);
        this.createSignature(MSG.quitSig1Desc.s,
                MyPlugin.QUIT_PERMISSION,
                new Parameter(MSG.quitSig1QuitMsg.s, Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(MSG.quitHelp.s);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {

        String message = MSG.quitDefaultQuitMsg.s;
        if (this.match(signature, 1)) {
            message = signature.getStringValue(0);
        }
        

        Conversation c = null;
        try {
            c = this.createConversation(executer, channel);
            c.writeLine(MSG.quitConfirm.s);
            String a = c.readLine().getMessage();
            
            for (String ans : answers) {
                if (a.equals(ans)) {
                    this.getMyPolly().irc().quit(message);
                    this.getMyPolly().shutdownManager().shutdown();
                    return false;
                }
            }
        } catch (InterruptedException e) {
            throw new CommandException(MSG.quitTimeout.s);
        } catch (Exception e) {
            throw new CommandException(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        

        return false;
    }
}
