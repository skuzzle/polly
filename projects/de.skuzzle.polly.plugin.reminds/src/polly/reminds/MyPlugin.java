package polly.reminds;


import http.RemindHttpController;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import commands.ToggleMailCommand;
import commands.DeleteRemindCommand;
import commands.LeaveCommand;
import commands.MailRemindCommand;
import commands.ModRemindCommand;
import commands.MyRemindsCommand;
import commands.OnReturnCommand;
import commands.RemindCommand;
import commands.SnoozeCommand;
import core.DeliverRemindHandler;
import core.RemindManager;
import core.RemindManagerImpl;
import core.RemindTraceNickchangeHandler;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.TimespanType;
import de.skuzzle.polly.sdk.constraints.Constraints;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Milliseconds;
import entities.RemindEntity;


/**
 * 
 * @author Simon
 * @version 27.07.2011 e1a9f7c
 */
public class MyPlugin extends PollyPlugin {
    
    public final static String REMIND_ROLE = "polly.roles.REMIND";
    
    public final static String DELETE_REMIND_PERMISSION = "polly.permission.DELETE_REMIND";
    public final static String LEAVE_PERMISSION         = "polly.permission.LEAVE";
    public final static String MAIL_REMIND_PERMISSION   = "polly.permission.MAIL_REMIND";
    public final static String MODIFY_REMIND_PERMISSION = "polly.permission.MODIFY_REMIND";
    public final static String MODIFY_OTHER_REMIND_PERMISSION = "polly.permission.MODIFY_OTHER_REMIND";
    public final static String MY_REMINDS_PERMISSION    = "polly.permission.MY_REMINDS";
    public final static String ON_RETURN_PERMISSION     = "polly.permission.ON_RETURN";
    public final static String REMIND_PERMISSION        = "polly.permission.REMIND";
    public final static String SNOOZE_PERMISSION        = "polly.permission.SNOOZE";
    public final static String TOGGLE_MAIL_PERMISSION   = "polly.permission.TOGGLE_MAIL";

    public final static String REMIND_FORMAT_NAME  = "REMIND_FORMAT";
    public final static StringType REMIND_FORMAT_VALUE = new Types.StringType(
        "@%r%: %m%. (Hinterlassen von: %s% am %ld%)");
    
    public final static String MESSAGE_FORMAT_NAME  = "MESSAGE_FORMAT";
    public final static Types MESSAGE_FORMAT_VALUE = new Types.StringType(
        "@%r%: %m%. (Hinterlassen von: %s% am %ld%)");
    
    public final static String SNOOZE_TIME          = "SNOOZE_TIME";
    public final static TimespanType SNOOZE_DEFAULT_VALUE = new Types.TimespanType(
        Milliseconds.fromMinutes(10) / 1000);
    
    public final static String USE_SNOOZE_TIME       = "USE_SNOOZE_TIME";
    public final static Types USE_SNOOZE_TIME_VALUE  = new Types.BooleanType(false);
    
    public final static String DEFAULT_REMIND_TIME = "DEFAULT_REMIND_TIME";
    public final static TimespanType DEFAULT_REMIND_TIME_VALUE = new Types.TimespanType(
        Milliseconds.fromMinutes(10) / 1000);
    
    public final static String DEFAULT_MSG       = "REMIND_DEFAULT_MSG";
    public final static Types DEFAULT_MSG_VALUE  = new Types.StringType("Reminder!");
	
	public final static String EMAIL         = "EMAIL";
	public final static Types DEFAULT_EMAIL = new Types.StringType("none");
	
	public final static String LEAVE_AS_MAIL         = "LEAVE_AS_MAIL";
	public final static Types DEFAULT_LEAVE_AS_MAIL  = new Types.BooleanType(false);
	
	public final static String REMIND_TRACK_NICKCHANGE         = "REMIND_TRACK_NICKCHANGE";
	public final static Types DEFAULT_REMIND_TRACK_NICKCHANGE  = new Types.BooleanType(false);
	
	public final static String REMIND_DOUBLE_DELIVERY         = "REMIND_DOUBLE_DELIVERY";
	public final static Types DEFAULT_REMIND_DOUBLE_DELIVERY = new Types.BooleanType(false);
	
	public final static String AUTO_SNOOZE      = "AUTO_SNOOZE";
	public final static Types AUTO_SNOOZE_VALUE = new Types.BooleanType(false);
	
	public final static String AUTO_SNOOZE_INDICATOR      = "AUTO_SNOOZE_INDICATOR";
	public final static Types AUTO_SNOOZE_INDICATOR_VALUE = new Types.StringType("k");
	
	public final static String REMIND_IDLE_TIME = "REMIND_IDLE_TIME";
    
	
	
    private Logger logger;
    private RemindManager remindManager;
    private RemindTraceNickchangeHandler remindNickChangeTracer;
    private DeliverRemindHandler deliverRemindHandler;

    

    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
                DuplicatedSignatureException {
        super(myPolly);
        
        this.logger = Logger.getLogger(myPolly.getLoggerName(this.getClass()));
        
        myPolly.persistence().registerEntity(RemindEntity.class);
        
        this.remindManager = new RemindManagerImpl(myPolly);
        
        this.deliverRemindHandler = new DeliverRemindHandler(this.remindManager, 
            myPolly.users());
        this.remindNickChangeTracer = new RemindTraceNickchangeHandler(
                this.remindManager);
        
        this.addDisposable(this.remindManager);
        
        this.addCommand(new LeaveCommand(myPolly, this.remindManager));
        this.addCommand(new OnReturnCommand(myPolly, this.remindManager));
        this.addCommand(new RemindCommand(myPolly, this.remindManager));
        this.addCommand(new MyRemindsCommand(myPolly, this.remindManager));
        this.addCommand(new DeleteRemindCommand(myPolly, this.remindManager));
        this.addCommand(new SnoozeCommand(myPolly, this.remindManager));
        this.addCommand(new ModRemindCommand(myPolly, this.remindManager));
        this.addCommand(new MailRemindCommand(myPolly, this.remindManager));
        this.addCommand(new ToggleMailCommand(myPolly, this.remindManager));
        
        final Controller ctrl = new RemindHttpController(myPolly, this.remindManager);
        this.getMyPolly().webInterface().getServer().addController(ctrl);
    }
    
    
    
    @Override
    public void actualDispose() throws DisposingException {
        this.getMyPolly().irc().removeNickChangeListener(this.remindNickChangeTracer);
        this.getMyPolly().irc().removeJoinPartListener(this.deliverRemindHandler);
        this.getMyPolly().irc().removeMessageListener(this.deliverRemindHandler);
        this.getMyPolly().users().removeUserListener(this.deliverRemindHandler);
        super.actualDispose();
    }       
    
    
    
    private void assignListeners() {
        this.getMyPolly().irc().addNickChangeListener(this.remindNickChangeTracer);
        this.getMyPolly().irc().addJoinPartListener(this.deliverRemindHandler);
        this.getMyPolly().irc().addMessageListener(this.deliverRemindHandler);
        this.getMyPolly().users().addUserListener(this.deliverRemindHandler);
    }

    
    
    @Override
    public Set<String> getContainedPermissions() {
        Set<String> result = new TreeSet<String>(super.getContainedPermissions());
        result.add(MODIFY_OTHER_REMIND_PERMISSION);
        return result;
    }
    
    
    
    @Override
    public void assignPermissions(RoleManager roleManager)
            throws RoleException, DatabaseException {
        super.assignPermissions(roleManager);
        
        roleManager.assignPermission(RoleManager.ADMIN_ROLE, MODIFY_OTHER_REMIND_PERMISSION);
        
        roleManager.createRole(REMIND_ROLE);
        roleManager.assignPermission(REMIND_ROLE, DELETE_REMIND_PERMISSION);
        roleManager.assignPermission(REMIND_ROLE, LEAVE_PERMISSION);
        roleManager.assignPermission(REMIND_ROLE, MAIL_REMIND_PERMISSION);
        roleManager.assignPermission(REMIND_ROLE, MODIFY_REMIND_PERMISSION);
        roleManager.assignPermission(REMIND_ROLE, MY_REMINDS_PERMISSION);
        roleManager.assignPermission(REMIND_ROLE, ON_RETURN_PERMISSION);
        roleManager.assignPermission(REMIND_ROLE, REMIND_PERMISSION);
        roleManager.assignPermission(REMIND_ROLE, SNOOZE_PERMISSION);
        roleManager.assignPermission(REMIND_ROLE, TOGGLE_MAIL_PERMISSION);
    }
    
    
    
    @Override
    public void onLoad() {
        
        logger.info("Scheduling all reminds...");
        this.remindManager.rescheduleAll();
        
        try {
            UserManager users = this.getMyPolly().users();
            final String category = "Reminds";
            
            users.addAttribute(REMIND_FORMAT_NAME, REMIND_FORMAT_VALUE, 
                "Set the format for your remind messages. Valid patterns are: "
                + "%m = message, %s = sender, %r = receiver, %d = duedate, %l = "
                + "leave date, %c = channel, %id = id", category);
            users.addAttribute(MESSAGE_FORMAT_NAME, MESSAGE_FORMAT_VALUE,
                "Set the format for your leave messages. Valid patterns are: "
                + "%m = message, %s = sender, %r = receiver, %d = duedate, %l = "
                + "leave date, %c = channel, %id = id", category);
            
            users.addAttribute(SNOOZE_TIME, SNOOZE_DEFAULT_VALUE, 
                "Set the timespan in which you may put your reminds to snooze. "
                + "If the time has passed, the remind will be deleted. Set to 0 if you "
                + "want your reminds snoozable forever", category,
                Constraints.TIMESPAN);
            users.addAttribute(DEFAULT_MSG, DEFAULT_MSG_VALUE, 
                "Remind default message:", category);
            users.addAttribute(EMAIL, DEFAULT_EMAIL, "Set your e-mail address", category, 
                Constraints.MAILADDRESS);
            users.addAttribute(LEAVE_AS_MAIL, DEFAULT_LEAVE_AS_MAIL,
                "Set whether polly should send you a mail if you are not available in IRC",
                category, Constraints.BOOLEAN);
            users.addAttribute(REMIND_IDLE_TIME, new TimespanType(User.IDLE_AFTER / 1000),
                "Set the time after which polly should consider you being 'idle'",
                category, Constraints.TIMESPAN);
            users.addAttribute(REMIND_TRACK_NICKCHANGE, DEFAULT_REMIND_TRACK_NICKCHANGE,
                "Deliver your reminds to your new nickname if you change your nick?",
                category, Constraints.BOOLEAN);
            users.addAttribute(REMIND_DOUBLE_DELIVERY, DEFAULT_REMIND_DOUBLE_DELIVERY, 
                "Deliver your remind when you are idle AND when you return? "
                + "If false, remind will always be delivered instantly",
                category, Constraints.BOOLEAN);
            users.addAttribute(DEFAULT_REMIND_TIME, DEFAULT_REMIND_TIME_VALUE,
                "Set default time span for new reminds", category, Constraints.TIMESPAN);
            users.addAttribute(AUTO_SNOOZE, AUTO_SNOOZE_VALUE, 
                "Enable auto snooze? If enabled, reminds can be snoozed by sending a "
                + "single character in query", category,
                Constraints.BOOLEAN);
            users.addAttribute(AUTO_SNOOZE_INDICATOR, AUTO_SNOOZE_INDICATOR_VALUE,
                "String to send to trigger auto snooze.", category);
            users.addAttribute(USE_SNOOZE_TIME, USE_SNOOZE_TIME_VALUE, 
                "Always use your default remind time when snoozing a remind with "
                + "no date parameter?", category,
                Constraints.BOOLEAN);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        
        this.assignListeners();
    }
    
    
    
    @Override
    public void uninstall() {
        try {
            this.getMyPolly().persistence().dropTable("REMINDENTITY");
        } catch (DatabaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
