package de.skuzzle.polly.core.internal.mypolly;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import de.skuzzle.polly.core.Polly;
import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.internal.commands.CommandManagerImpl;
import de.skuzzle.polly.core.internal.conversations.ConversationManagerImpl;
import de.skuzzle.polly.core.internal.formatting.FormatManagerImpl;
import de.skuzzle.polly.core.internal.httpv2.WebInterfaceManagerImpl;
import de.skuzzle.polly.core.internal.irc.IrcManagerImpl;
import de.skuzzle.polly.core.internal.mail.MailManagerImpl;
import de.skuzzle.polly.core.internal.paste.PasteServiceManagerImpl;
import de.skuzzle.polly.core.internal.persistence.PersistenceManagerV2Impl;
import de.skuzzle.polly.core.internal.plugins.PluginManagerImpl;
import de.skuzzle.polly.core.internal.roles.RoleManagerImpl;
import de.skuzzle.polly.core.internal.runonce.RunOnceManagerImpl;
import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.core.parser.InputParser;
import de.skuzzle.polly.core.parser.InputScanner;
import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.expressions.literals.DateLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.ParentSetter;
import de.skuzzle.polly.core.parser.ast.visitor.resolving.TypeResolver;
import de.skuzzle.polly.core.parser.problems.ProblemReporter;
import de.skuzzle.polly.core.parser.problems.SimpleProblemReporter;
import de.skuzzle.polly.core.util.TypeMapper;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.sdk.ConversationManager;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.MailManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PluginManager;
import de.skuzzle.polly.sdk.RunOnceManager;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.UtilityManager;
import de.skuzzle.polly.sdk.eventlistener.GenericEvent;
import de.skuzzle.polly.sdk.eventlistener.GenericListener;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.paste.PasteServiceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.DateUtils;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.events.EventProvider;



/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class MyPollyImpl extends AbstractDisposable implements MyPolly {
    
    private final static Logger logger = Logger.getLogger(MyPollyImpl.class.getName());
    
    private final static String[] DAYS = {"montag", "dienstag", "mittwoch", 
        "donnerstag", "freitag", "samstag", "sonntag"};
    
    
	private CommandManagerImpl commandManager;
	private IrcManagerImpl ircManager;
	private PluginManagerImpl pluginManager;
	private ConfigurationProviderImpl configProvider;
	private PersistenceManagerV2Impl persistence;
	private UserManagerImpl userManager;
	private FormatManagerImpl formatManager;
	private ConversationManagerImpl conversationManager;
	private ShutdownManagerImpl shutdownManager;
	private Date startTime;
	private PasteServiceManagerImpl pasteManager;
	private MailManagerImpl mailManager;
	private RoleManagerImpl roleManager;
	private WebInterfaceManagerImpl webInterfaceManager;
	private RunOnceManagerImpl runOnceManager;
	private EventProvider eventProvider;
	private final Map<String, String> status;
	
	
	public MyPollyImpl(CommandManagerImpl cmdMngr, 
	        IrcManagerImpl ircMngr, 
			PluginManagerImpl plgnMngr, 
			ConfigurationProviderImpl configProviderImpl, 
			PersistenceManagerV2Impl pMngr,
			UserManagerImpl usrMngr,
			FormatManagerImpl fmtMngr,
			ConversationManagerImpl convMngr,
			ShutdownManagerImpl shutdownManager,
			PasteServiceManagerImpl pasteManager,
			MailManagerImpl mailManager,
			RoleManagerImpl roleManager,
			WebInterfaceManagerImpl webInterfaceManager,
			RunOnceManagerImpl runOnceManager,
			EventProvider eventProvider) {
	
	    this.status = new HashMap<>();
		this.commandManager = cmdMngr;
		this.ircManager = ircMngr;
		this.pluginManager = plgnMngr;
		this.configProvider = configProviderImpl;
		this.persistence = pMngr;
		this.userManager = usrMngr;
		this.formatManager = fmtMngr;
		this.conversationManager = convMngr;
		this.shutdownManager = shutdownManager;
		this.pasteManager = pasteManager;
		this.startTime = Time.currentTime();
		this.mailManager = mailManager;
		this.roleManager = roleManager;
		this.webInterfaceManager = webInterfaceManager;
		this.runOnceManager = runOnceManager;
	}
	
	
	
	@Override
	public void setStatus(String key, String status) {
	    this.status.put(key, status);
	}
	
	
	
	@Override
	public Map<String, String> getStatusMap() {
	    return this.status;
	}
	
	
	
	@Override
	public Types parse(String value) {
        final User executor = this.users().getAdmin();
        
        final ProblemReporter reporter = new SimpleProblemReporter();
        final InputScanner is = new InputScanner(value);
        final InputParser ip = new InputParser(is, reporter);
        is.setSkipWhiteSpaces(true);
        
        try {
            final Expression exp = ip.parseSingleExpression();
            exp.visit(new ParentSetter());
            
            final String nsName = executor.getCurrentNickName() == null 
                ? executor.getName() 
                : executor.getCurrentNickName(); 
                

            final Namespace ns = Namespace.forName(nsName);
            final Namespace workingNs = ns.enter();
            
            
            int m = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            workingNs.declare(new Declaration(Position.NONE, new Identifier("morgen"), 
                new DateLiteral(Position.NONE, DateUtils.getDayDate(m + 1))));
            workingNs.declare(new Declaration(Position.NONE, new Identifier("übermorgen"), 
                new DateLiteral(Position.NONE, DateUtils.getDayDate(m + 2))));
            
            
            int start = Calendar.MONDAY;
            for (String day : DAYS) {
                workingNs.declare(new Declaration(Position.NONE, new Identifier(day), 
                    new DateLiteral(Position.NONE, DateUtils.getDayDate(start++))));
            }
            
            
            // resolve types
            TypeResolver.resolveAST(exp, workingNs, reporter);
            
            final ExecutionVisitor exec = new ExecutionVisitor(ns, workingNs, reporter);
            exp.visit(exec);
            final Literal result = exec.getSingleResult();
            return TypeMapper.literalToTypes(result);
        } catch (ASTTraversalException e) {
            logger.warn("", e);
            // ignore the exception, just use plain value which was submitted
            return new Types.StringType(value);
        }
	}
	
	
	@Override
	public WebinterfaceManager webInterface() {
	    return this.webInterfaceManager;
	}
	
	
	
	@Override
	public RunOnceManager runOnce() {
	    return this.runOnceManager;
	}
	
	

	@Override
	public IrcManager irc() {
		return this.ircManager;
	}

	
	
	@Override
	public String getPollyVersion() {
		return Polly.class.getPackage().getImplementationVersion();
	}

	
	
	@Override
	public PersistenceManagerV2 persistence() {
		return this.persistence;
	}

	
	
	@Override
	public UserManager users() {
		return this.userManager;
	}


	@Override
	public CommandManager commands() {
		return this.commandManager;
	}



	@Override
	public PluginManager plugins() {
		return this.pluginManager;
	}



	@Override
	public synchronized void shutdown() {
	    this.shutdownManager.shutdown();
	}
	
	
	@Override
	public ShutdownManagerImpl shutdownManager() {
	    return this.shutdownManager;
	}
	
	
	
	@Deprecated
	public void shutdown(boolean exit) {
	    this.shutdownManager.shutdown(exit);
	}



	@Override
	public ConfigurationProvider configuration() {
		return this.configProvider;
	}

	
	
	@Override
	public PasteServiceManager pasting() {
	    return this.pasteManager;
	}
	
	
	
	@Override
	public UtilityManager utilities() {
	    return new UtilityManager() {/* TODO: utilities */};
	}
	

	@Override
	public String getLoggerName(Class<?> clazz) {
		return "polly.Plugin." + clazz.getName();
	}



    @Override
    public FormatManager formatting() {
        return this.formatManager;
    }
    
    
    
    @Override
    public ConversationManager conversations() {
        return this.conversationManager;
    }
    
    
    
    public Date getStartTime() {
        return this.startTime;
    }

    
    
    public boolean isDebugMode() {
        return this.configProvider.getRootConfiguration().readBoolean(
            Configuration.DEBUG_MODE);
    }
    
    
    
    @Override
    public MailManager mails() {
        return this.mailManager;
    }
    

    @Override
    protected void actualDispose() throws DisposingException {
        this.shutdown();
    }



    @Override
    public RoleManager roles() {
        return this.roleManager;
    }



    @Override
    public void addGenericListener(GenericListener listener) {
        this.eventProvider.addListener(GenericListener.class, listener);
    }



    @Override
    public void removeGenericListener(GenericListener listener) {
        this.eventProvider.removeListener(GenericListener.class, listener);
    }



    @Override
    public void fireGenericEvent(final GenericEvent e) {
        this.eventProvider.dispatchEvent(GenericListener.class, e, 
                GenericListener.GENERIC_EVENT);
    }
}
