package polly.core;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import polly.PollyConfiguration;
import polly.util.MillisecondStopwatch;
import polly.util.Stopwatch;
import de.skuzzle.polly.parsing.AbstractParser;
import de.skuzzle.polly.parsing.Context;
import de.skuzzle.polly.parsing.Declarations;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.PollyParserFactory;
import de.skuzzle.polly.parsing.SyntaxMode;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.tree.ChannelLiteral;
import de.skuzzle.polly.parsing.tree.Expression;
import de.skuzzle.polly.parsing.tree.IdentifierLiteral;
import de.skuzzle.polly.parsing.tree.ListLiteral;
import de.skuzzle.polly.parsing.tree.Literal;
import de.skuzzle.polly.parsing.tree.Root;
import de.skuzzle.polly.parsing.tree.UserLiteral;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.exceptions.UnknownSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class CommandExecutor {

    private static Logger logger = Logger.getLogger(CommandExecutor.class.getName());
    private UserManagerImpl userManager;
    private PollyConfiguration config;
    private CommandManager commands;
    
    
    
    public Runnable getRunnable(final MessageEvent e, final User executor)  throws 
               UnsupportedEncodingException, 
               ParseException, 
               UnknownSignatureException {
        
        
        logger.trace("Creating local context for command execution");
        Context c = this.createContext(e, executor);
        Root root = this.parseMessage(e, c);
        
        logger.trace("Resolving signature");
        final Signature sig = this.createSignature(root);
        final Command cmd = this.commands.getCommand(sig);
        
        return new Runnable() {
            
            @Override
            public void run() {
                Stopwatch watch = new MillisecondStopwatch();
                watch.start();
                
                try {
                    logger.debug("Executing '" + cmd + "' on channel " + 
                        e.getChannel());
                    
                    cmd.doExecute(executor, e.getChannel(), e.inQuery(), sig);
                } catch (InsufficientRightsException e1) {
                    logger.error("Could not execute command " + cmd.getCommandName() + 
                        " by user " + executor.getName(), e1);
                    e.getSource().sendMessage(e.getChannel(), "Du kannst den Befehl '" + 
                        cmd.getCommandName() + "' nicht ausführen.");
                    
                } catch (CommandException e1) {
                    logger.error("Exception while executing command " + 
                        cmd.getCommandName(), e1);
                    e.getSource().sendMessage(e1.getMessage(), e.getChannel(), cmd);
                } finally {
                    watch.stop();
                    logger.trace("Execution time: " + watch.getDifference() + "ms");
                }
            }
        };
    }
    
    
    
    private Root parseMessage(MessageEvent e, Context c) 
            throws UnsupportedEncodingException, ParseException {
        
        Stopwatch watch = new MillisecondStopwatch();
        watch.start();

        try {
            AbstractParser<?> parser = PollyParserFactory.createParser(
                    SyntaxMode.POLLY_CLASSIC);
            
            Root root = (Root) parser.parse(e.getMessage().trim(), 
                this.config.getEncodingName()); 
            
            if (root == null) {
                return null;
            }
        
            logger.trace("Parsed input '" + e.getMessage() + "'");
            
            logger.trace("Starting context check");
            root.contextCheck(c);
            
            logger.trace("Collapsing all parameters");
            root.collapse(new Stack<Literal>());
            
            return root;
        } finally {
            watch.stop();
            logger.trace("Parsing time: " + watch.getDifference() + "ms");
        }
    }
    
    
    
    private Context createContext(MessageEvent e, User user) throws ParseException{
        Declarations d = this.userManager.getDeclarations(user);
        
        List<Expression> channels = new ArrayList<Expression>();
        for (String channel : e.getSource().getChannels()) {
            channels.add(new ChannelLiteral(channel));
        }
        
        // ISSUE: 0000008
        List<Expression> users = new ArrayList<Expression>();
        for (String u : e.getSource().getChannelUser(e.getChannel())) {
            users.add(new UserLiteral(u));
        }
        d.add(new IdentifierLiteral("me"), new UserLiteral(e.getUser().getNickName()));
        d.add(new IdentifierLiteral("here"), new ChannelLiteral(e.getChannel()));
        d.add(new IdentifierLiteral("all"), new ListLiteral(channels, Type.CHANNEL));
        d.add(new IdentifierLiteral("each"), new ListLiteral(users, Type.USER));

        logger.trace("    me   := " + e.getUser().getNickName());
        logger.trace("    here := " + e.getChannel());
        logger.trace("    all  := " + channels.toString());
        logger.trace("    each := " + users);
        
        //d = Declarations.createContext(d);
        return new Context(d, this.userManager.getNamespaces());
    }
    
    
    
    private Signature createSignature(Root root) throws UnknownSignatureException {
        List<Types> parameters = new ArrayList<Types>();
        for (Literal lit : root.getResults()) {
            parameters.add(TypeMapper.literalToTypes(lit));
        }
        return new Signature(root.getName().getCommandName(), -1, parameters);
    }
}