package de.skuzzle.polly.core.eventhandler;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import de.skuzzle.polly.core.internal.users.UserImpl;
import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.exceptions.UnknownCommandException;
import de.skuzzle.polly.sdk.exceptions.UnknownSignatureException;



public class MessageHandler implements MessageListener {
    
    private final static int OFF = 0;
    private final static int SIMPLE = 1;

    
	private static Logger logger = Logger.getLogger(MessageHandler.class.getName());
    private CommandManager commands;
    private UserManagerImpl userManager;
    private ExecutorService executorThreadPool;
    private int parseErrorDetails;
    private boolean reportUnknownCommand;
    
    
    
    public MessageHandler(CommandManager commandManager, UserManagerImpl userManager, 
            ExecutorService executorThreadPool, int parseErrorDetails, 
            boolean reportUnknownCommand) {
        this.commands = commandManager;
        this.userManager = userManager;
        this.executorThreadPool = executorThreadPool;
        this.parseErrorDetails = parseErrorDetails;
        this.reportUnknownCommand = reportUnknownCommand;
    }
    

    
    @Override
    public void publicMessage(MessageEvent e) {
        this.execute(e, false);
    }

    
    
    @Override
    public void privateMessage(MessageEvent e) {
        this.execute(e, true);
    }

    
    
    @Override
    public void actionMessage(MessageEvent e) {
        this.getUser(e.getUser()).setLastMessageTime(System.currentTimeMillis());
    }
    
    
    
    private void execute(final MessageEvent e, final boolean isQuery) {
        final de.skuzzle.polly.core.internal.users.UserImpl executor = this.getUser(e.getUser());
        executor.setLastMessageTime(System.currentTimeMillis());
        
        Runnable command = new Runnable() {
            @Override
            public void run() {
                try {
                    MessageHandler.this.commands.executeString(e.getMessage(), e
                        .getChannel(), isQuery, executor, e.getSource());
                } catch(CommandException e1) {
                    if (e1.getCause() instanceof ASTTraversalException) {
                        ASTTraversalException e2 = (ASTTraversalException) e1.getCause();
                        MessageHandler.this.reportParseError(e, e2);
                    } else {
                        e.getSource().sendMessage(e.getChannel(), 
                            MSG.bind(MSG.cmdExecError, e1.getMessage()), this);
                    }
                    logger.debug("", e1); //$NON-NLS-1$
                } catch (UnknownCommandException e1) {
                    if (reportUnknownCommand) {
                        e.getSource().sendMessage(e.getChannel(), 
                                MSG.bind(MSG.cmdUnknown, e.getMessage()), this);
                    }
                } catch (UnknownSignatureException e1) {
                    e.getSource().sendMessage(e.getChannel(), 
                            MSG.bind(MSG.cmdUnknownSignature, e1.getSignature().toString()), 
                            this);
                } catch (InsufficientRightsException e1) {
                    e.getSource().sendMessage(e.getChannel(), 
                            MSG.bind(MSG.cmdInsufficientRights, e1.getObject(), 
                                    e1.getObject().getRequiredPermission()), this);
                } catch (Exception e1) {
                    logger.error("Exception while executing command: " +  //$NON-NLS-1$
                            e1.getMessage(), e1);
                    e.getSource().sendMessage(e.getChannel(), MSG.cmdInternalError, this);
                }
            }
        };
        
        this.executorThreadPool.execute(command);
    }
    
    
    
    private void reportParseError(MessageEvent e, ASTTraversalException ex) {
        int detail = this.parseErrorDetails;
        if (detail == OFF) {
            return;
        }
        
        if (detail > OFF) {
            e.getSource().sendMessage(e.getChannel(), ex.getMessage());
        }
        if (detail > SIMPLE) {
            String msg = ex.getPosition().mark(e.getMessage());
            e.getSource().sendMessage(e.getChannel(), msg);
        }
    }
    
    
    
    private de.skuzzle.polly.core.internal.users.UserImpl getUser(IrcUser user) {
        UserImpl u = (UserImpl) this.userManager.getUser(user);
        if (u == null) {
            u = (UserImpl) this.userManager.createUser("~UNKNOWN", "blabla");  //$NON-NLS-1$//$NON-NLS-2$
            u.setCurrentNickName(user.getNickName());
        }
        return u;
    }



    @Override
    public void noticeMessage(MessageEvent e) {
        this.getUser(e.getUser()).setLastMessageTime(System.currentTimeMillis());
    }
}
