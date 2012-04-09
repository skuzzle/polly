package de.skuzzle.polly.sdk;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;



/**
 * <p>This extends a normal command to remember the time of last execution per user to 
 * define execution delays.</p>
 * 
 * <p>You can set the quiet attribute to <code>false</code> if you do not want users to be
 * notified that they can not execute the command again in such short delay.</p>
 * 
 * @author Simon
 * @since 0.9
 * @see Command
 */
public abstract class DelayedCommand extends Command {

    private int delay;
    private boolean quiet;
    private Map<User, Long> lastExecutions;
    
    
    
    /**
     * Creates a new delayed Command. See the documentation of {@link Command} for 
     * detailed information on how to use commands. This delayed command is not quiet
     * and thus will report execution errors caused by too short execution times.
     * 
     * @param polly The MyPolly instance.
     * @param commandName The command name.
     * @param delay The execution delay in milliseconds for this command.
     * @see Command
     */
    public DelayedCommand(MyPolly polly, String commandName, int delay) {
        this(polly, commandName, delay, false);
    }
    
    
    
    /**
     * Creates a new delayed Command. See the documentation of {@link Command} for 
     * detailed information on how to use commands. You can define whether this command
     * should be quiet or not. If quiet is set to <code>true</code>, this command will
     * simply ignore consecutive executions with too short delay. If set to 
     * <code>false</code>, an error will be reported.
     * 
     * @param polly The MyPolly instance.
     * @param commandName The command name.
     * @param delay The execution delay in milliseconds for this command.
     * @param quiet Whether the command should be quiet or not.
     */
    public DelayedCommand(MyPolly polly, String commandName, int delay, boolean quiet) {
        super(polly, commandName);
        this.delay = delay;
        this.lastExecutions = new HashMap<User, Long>();
        this.quiet = quiet;
    }
    
    
    
    /**
     * Sets whether this command is quiet or not.
     * 
     * @param quiet <code>true</code> if you want errors caused by too short consecutive
     *          executions to be ignored or <code>false</code> if you want them to be
     *          reported. 
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }
    
    
    
    /**
     * Gets whether this command is quiet.
     * 
     * @return <code>true</code> if this command is quiet.
     */
    public boolean isQuiet() {
        return this.quiet;
    }
    
    
    
    /**
     * Sets the delay in milliseconds for this command. A user who executes this command
     * has to wait at least the amount of milliseconds given here before executing it 
     * again.
     *  
     * @param delay The execution delay in ms.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    
    
    /**
     * Gets the execution delay in milliseconds for this command.
     * 
     * @return The execution delay.
     */
    public int getDelay() {
        return this.delay;
    }
    
    
    
    @Override
    public void doExecute(User executer, String channel, boolean query, 
            Signature signature) throws InsufficientRightsException, CommandException {
        
        if (!this.checkDelay(executer)) {
            return;
        }
        
        try {
            super.doExecute(executer, channel, query, signature);
            this.rememberExecution(executer);
        } catch (InsufficientRightsException e) {
            // do not remember execution if user has no rights to execute the command
            throw e;
        } catch (CommandException e) {
            // remember execution even if error occurred
            this.rememberExecution(executer);
            throw e;
        }
    }
    
    
    
    /**
     * Stores the current Polly system time as provided by 
     * {@link MyPolly#currentTimeMillis()} as last execution for the given user.
     * 
     * @param user The user who executes this command.
     */
    protected void rememberExecution(User user) {
        synchronized (this.lastExecutions) {
            this.lastExecutions.put(user, this.getMyPolly().currentTimeMillis());
        }
    }

    
    
    /**
     * Checks whether the given user has waited at least the delay of this command before
     * executing it again.
     * 
     * @param user The user to check.
     * @return <code>true</code> if the user is allowed to execute the command again.
     * @throws CommandException If this command is not quiet, the exception informs the
     *          user how long he has to wait before executing the command again.
     */
    protected boolean checkDelay(User user) throws CommandException {
        Long i = null;
        synchronized (this.lastExecutions) {
            i = this.lastExecutions.get(user);
        }
        
        long diff = 0L;
        if (i == null || (diff = this.getMyPolly().currentTimeMillis() - i) >= this.delay) {
            return true;
        }
        
        if (this.quiet) {
            return false;
        }
        
        long remaining = (this.delay - diff) / 1000; 
        String f = this.getMyPolly().formatting().formatTimeSpan(remaining);
        throw new CommandException("Du kannst den Befehl erst in " + f + 
                " wieder ausführen.");
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        this.lastExecutions.clear();
        this.lastExecutions = null;
    }
}