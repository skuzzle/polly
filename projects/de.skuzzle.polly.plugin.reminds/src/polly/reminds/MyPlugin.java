package polly.reminds;


import http.AllRemindsTableModel;
import http.MyRemindTableModel;
import http.RemindHttpController;
import http.RemindTableFilter;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import commands.DeleteRemindCommand;
import commands.LeaveCommand;
import commands.MailRemindCommand;
import commands.ModRemindCommand;
import commands.MyRemindsCommand;
import commands.OnReturnCommand;
import commands.RemindCommand;
import commands.SnoozeCommand;
import commands.ToggleMailCommand;
import core.DeliverRemindHandler;
import core.RemindManager;
import core.RemindManagerImpl;
import core.RemindTraceNickchangeHandler;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.Types.TimespanType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.constraints.Constraints;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.httpv2.MenuCategory;
import de.skuzzle.polly.sdk.httpv2.html.HTMLColumnFilter;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTable;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTableModel;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Milliseconds;
import entities.RemindEntity;


/**
 * 
 * @author Simon
 * @version 27.07.2011 e1a9f7c
 */
public class MyPlugin extends PollyPlugin {
    
    public final static String REMIND_ROLE = "polly.roles.REMIND"; //$NON-NLS-1$
    
    public final static String DELETE_REMIND_PERMISSION = "polly.permission.DELETE_REMIND"; //$NON-NLS-1$
    public final static String LEAVE_PERMISSION         = "polly.permission.LEAVE"; //$NON-NLS-1$
    public final static String MAIL_REMIND_PERMISSION   = "polly.permission.MAIL_REMIND"; //$NON-NLS-1$
    public final static String MODIFY_REMIND_PERMISSION = "polly.permission.MODIFY_REMIND"; //$NON-NLS-1$
    public final static String MODIFY_OTHER_REMIND_PERMISSION = "polly.permission.MODIFY_OTHER_REMIND"; //$NON-NLS-1$
    public final static String MY_REMINDS_PERMISSION    = "polly.permission.MY_REMINDS"; //$NON-NLS-1$
    public final static String ON_RETURN_PERMISSION     = "polly.permission.ON_RETURN"; //$NON-NLS-1$
    public final static String REMIND_PERMISSION        = "polly.permission.REMIND"; //$NON-NLS-1$
    public final static String SNOOZE_PERMISSION        = "polly.permission.SNOOZE"; //$NON-NLS-1$
    public final static String TOGGLE_MAIL_PERMISSION   = "polly.permission.TOGGLE_MAIL"; //$NON-NLS-1$

    public final static String REMIND_FORMAT_NAME  = "REMIND_FORMAT"; //$NON-NLS-1$
    public final static StringType REMIND_FORMAT_VALUE = new Types.StringType(
            MSG.remindFormatValue);
    
    public final static String MESSAGE_FORMAT_NAME  = "MESSAGE_FORMAT"; //$NON-NLS-1$
    public final static Types MESSAGE_FORMAT_VALUE = new Types.StringType(
            MSG.remindFormatValue);
    
    public final static String SNOOZE_TIME          = "SNOOZE_TIME"; //$NON-NLS-1$
    public final static TimespanType SNOOZE_DEFAULT_VALUE = new Types.TimespanType(
        Milliseconds.fromMinutes(10) / 1000);
    
    public final static String USE_SNOOZE_TIME       = "USE_SNOOZE_TIME"; //$NON-NLS-1$
    public final static Types USE_SNOOZE_TIME_VALUE  = new Types.BooleanType(false);
    
    public final static String DEFAULT_REMIND_TIME = "DEFAULT_REMIND_TIME"; //$NON-NLS-1$
    public final static TimespanType DEFAULT_REMIND_TIME_VALUE = new Types.TimespanType(
        Milliseconds.fromMinutes(10) / 1000);
    
    public final static String DEFAULT_MSG       = "REMIND_DEFAULT_MSG"; //$NON-NLS-1$
    public final static Types DEFAULT_MSG_VALUE  = new Types.StringType("Reminder!"); //$NON-NLS-1$
	
	public final static String EMAIL         = "EMAIL"; //$NON-NLS-1$
	public final static Types DEFAULT_EMAIL = new Types.StringType("none"); //$NON-NLS-1$
	
	public final static String LEAVE_AS_MAIL         = "LEAVE_AS_MAIL"; //$NON-NLS-1$
	public final static Types DEFAULT_LEAVE_AS_MAIL  = new Types.BooleanType(false);
	
	public final static String REMIND_TRACK_NICKCHANGE         = "REMIND_TRACK_NICKCHANGE"; //$NON-NLS-1$
	public final static Types DEFAULT_REMIND_TRACK_NICKCHANGE  = new Types.BooleanType(false);
	
	public final static String REMIND_DOUBLE_DELIVERY         = "REMIND_DOUBLE_DELIVERY"; //$NON-NLS-1$
	public final static Types DEFAULT_REMIND_DOUBLE_DELIVERY  = new Types.BooleanType(false);
	
	public final static String AUTO_SNOOZE      = "AUTO_SNOOZE"; //$NON-NLS-1$
	public final static Types AUTO_SNOOZE_VALUE = new Types.BooleanType(false);
	
	public final static String AUTO_SNOOZE_INDICATOR      = "AUTO_SNOOZE_INDICATOR"; //$NON-NLS-1$
	public final static Types AUTO_SNOOZE_INDICATOR_VALUE = new Types.StringType("k"); //$NON-NLS-1$
	
	public final static String REMIND_IDLE_TIME = "REMIND_IDLE_TIME"; //$NON-NLS-1$
    
	
	
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
        
        
        myPolly.webInterface().addCategory(new MenuCategory(1, MSG.remindCategory));
        final Controller ctrl = new RemindHttpController(myPolly, this.remindManager);
        this.getMyPolly().webInterface().getServer().addController(ctrl);

        // set up remind tables
        final HTMLColumnFilter filter = new RemindTableFilter(myPolly);
        
        final HTMLTableModel<RemindEntity> myRemindsModel = new MyRemindTableModel(this.remindManager);
        final HTMLTableModel<RemindEntity> allRemindsModel = new AllRemindsTableModel(this.remindManager);
        
        final HTMLTable<RemindEntity> myRemindsTable = new HTMLTable<>("myReminds", myRemindsModel, myPolly); //$NON-NLS-1$
        final HTMLTable<RemindEntity> allRemindsTable = new HTMLTable<>("allReminds", allRemindsModel, myPolly); //$NON-NLS-1$
        myRemindsTable.setFilter(filter);
        allRemindsTable.setFilter(filter);
        
        this.getMyPolly().webInterface().getServer().addHttpEventHandler("/api/myReminds", myRemindsTable); //$NON-NLS-1$
        this.getMyPolly().webInterface().getServer().addHttpEventHandler("/api/allReminds", allRemindsTable); //$NON-NLS-1$
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
        
        logger.info("Scheduling all reminds..."); //$NON-NLS-1$
        this.remindManager.rescheduleAll();
        
        try {
            UserManager users = this.getMyPolly().users();
            final String category = MSG.remindCategory;
            
            users.addAttribute(REMIND_FORMAT_NAME, REMIND_FORMAT_VALUE, 
                    MSG.remindFormatDesc, category);
            users.addAttribute(MESSAGE_FORMAT_NAME, MESSAGE_FORMAT_VALUE,
                MSG.remindFormatDesc, category);
            users.addAttribute(SNOOZE_TIME, SNOOZE_DEFAULT_VALUE, 
                MSG.remindSnoozeDesc, category, Constraints.TIMESPAN);
            users.addAttribute(DEFAULT_MSG, DEFAULT_MSG_VALUE, 
                MSG.remindDefaultMsgDesc, category);
            users.addAttribute(EMAIL, DEFAULT_EMAIL, MSG.remindEmailDesc, category, 
                Constraints.MAILADDRESS);
            users.addAttribute(LEAVE_AS_MAIL, DEFAULT_LEAVE_AS_MAIL,
                MSG.remindLeaveAsMailDesc,
                category, Constraints.BOOLEAN);
            users.addAttribute(REMIND_IDLE_TIME, new TimespanType(User.IDLE_AFTER / 1000),
                MSG.remindIdleTimeDesc,
                category, Constraints.TIMESPAN);
            users.addAttribute(REMIND_TRACK_NICKCHANGE, DEFAULT_REMIND_TRACK_NICKCHANGE,
                MSG.remindTrackNickchangeDesc,
                category, Constraints.BOOLEAN);
            users.addAttribute(REMIND_DOUBLE_DELIVERY, DEFAULT_REMIND_DOUBLE_DELIVERY, 
                MSG.remindDoubleDeliveryDesc,
                category, Constraints.BOOLEAN);
            users.addAttribute(DEFAULT_REMIND_TIME, DEFAULT_REMIND_TIME_VALUE,
                MSG.remindDefaultRemindTimeDesc, category, Constraints.TIMESPAN);
            users.addAttribute(AUTO_SNOOZE, AUTO_SNOOZE_VALUE, 
                MSG.remindAutoSnoozeDesc, category,
                Constraints.BOOLEAN);
            users.addAttribute(AUTO_SNOOZE_INDICATOR, AUTO_SNOOZE_INDICATOR_VALUE,
                MSG.remindAutoSnoozeIndiDesc, category);
            users.addAttribute(USE_SNOOZE_TIME, USE_SNOOZE_TIME_VALUE, 
                MSG.remindUseSnoozeTimeDesc, category,
                Constraints.BOOLEAN);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        
        this.assignListeners();
    }
}
