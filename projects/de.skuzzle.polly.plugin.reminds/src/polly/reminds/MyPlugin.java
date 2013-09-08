package polly.reminds;


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
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
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
    public final static String REMIND_FORMAT_VALUE = "@%r%: %m%. (Hinterlassen von: %s% am %ld%)";
    
    public final static String MESSAGE_FORMAT_NAME  = "MESSAGE_FORMAT";
    public final static String MESSAGE_FORMAT_VALUE = "@%r%: %m%. (Hinterlassen von: %s% am %ld%)";
    
    public final static String SNOOZE_TIME          = "SNOOZE_TIME";
    public final static String SNOOZE_DEFAULT_VALUE = "10m";
    
    public final static String USE_SNOOZE_TIME       = "USE_SNOOZE_TIME";
    public final static String USE_SNOOZE_TIME_VALUE = "false";
    
    public final static String DEFAULT_REMIND_TIME = "DEFAULT_REMIND_TIME";
    public final static String DEFAULT_REMIND_TIME_VALUE = "10m";
    
    public final static String DEFAULT_MSG       = "REMIND_DEFAULT_MSG";
    public final static String DEFAULT_MSG_VALUE = "Reminder!";
	
	public final static String EMAIL         = "EMAIL";
	public final static String DEFAULT_EMAIL = "none";
	
	public final static String LEAVE_AS_MAIL         = "LEAVE_AS_MAIL";
	public final static String DEFAULT_LEAVE_AS_MAIL = "false";
	
	public final static String REMIND_TRACK_NICKCHANGE         = "REMIND_TRACK_NICKCHANGE";
	public final static String DEFAULT_REMIND_TRACK_NICKCHANGE = "true";
	
	public final static String REMIND_DOUBLE_DELIVERY         = "REMIND_DOUBLE_DELIVERY";
	public final static String DEFAULT_REMIND_DOUBLE_DELIVERY = "false";
	
	public final static String AUTO_SNOOZE = "AUTO_SNOOZE";
	public final static String AUTO_SNOOZE_VALUE = "false";
	
	public final static String AUTO_SNOOZE_INDICATOR = "AUTO_SNOOZE_INDICATOR";
	public final static String AUTO_SNOOZE_INDICATOR_VALUE = "k";
	
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
            
            users.addAttribute(REMIND_FORMAT_NAME, REMIND_FORMAT_VALUE);
            users.addAttribute(MESSAGE_FORMAT_NAME, MESSAGE_FORMAT_VALUE);
            users.addAttribute(SNOOZE_TIME, SNOOZE_DEFAULT_VALUE, Constraints.TIMESPAN);
            users.addAttribute(DEFAULT_MSG, DEFAULT_MSG_VALUE);
            users.addAttribute(EMAIL, DEFAULT_EMAIL, Constraints.MAILADDRESS);
            users.addAttribute(LEAVE_AS_MAIL, DEFAULT_LEAVE_AS_MAIL, Constraints.BOOLEAN);
            users.addAttribute(REMIND_IDLE_TIME, 
                "" + Milliseconds.toSeconds(User.IDLE_AFTER) + "s", 
                Constraints.TIMESPAN);
            users.addAttribute(REMIND_TRACK_NICKCHANGE, DEFAULT_REMIND_TRACK_NICKCHANGE, 
                Constraints.BOOLEAN);
            users.addAttribute(REMIND_DOUBLE_DELIVERY, DEFAULT_REMIND_DOUBLE_DELIVERY, 
                Constraints.BOOLEAN);
            users.addAttribute(DEFAULT_REMIND_TIME, DEFAULT_REMIND_TIME_VALUE, 
                Constraints.TIMESPAN);
            users.addAttribute(AUTO_SNOOZE, AUTO_SNOOZE_VALUE, Constraints.BOOLEAN);
            users.addAttribute(AUTO_SNOOZE_INDICATOR, AUTO_SNOOZE_INDICATOR_VALUE);
            users.addAttribute(USE_SNOOZE_TIME, USE_SNOOZE_TIME_VALUE, 
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
