package polly.reminds;

import java.util.List;

import org.apache.log4j.Logger;

import commands.DeleteRemindCommand;
import commands.LeaveCommand;
import commands.MyRemindsCommand;
import commands.RemindCommand;
import commands.RemindSettingsCommand;
import commands.SleepCommand;

import core.DeliverRemindHandler;
import core.RemindManager;
import core.RemindTraceNickchangeHandler;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.eventlistener.JoinPartListener;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import entities.RemindEntity;


/**
 * 
 * @author Simon
 * @version 27.07.2011 e1a9f7c
 */
public class MyPlugin extends PollyPlugin {

    public final static String REMIND_FORMAT_NAME = "REMIND_FORMAT";
    public final static String REMIND_FORMAT_VALUE = "@%r%: %m%. (Hinterlassen von: %s% am %ld%)";
    
    public final static String MESSAGE_FORMAT_NAME = "MESSAGE_FORMAT";
    public final static String MESSAGE_FORMAT_VALUE = "@%r%: %m%. (Hinterlassen von: %s% am %ld%)";
    
    public final static String SLEEP_TIME = "SLEEP_TIME";
    public final static String SLEEP_DEFAULT_VALUE = "60000";
    
    private Logger logger;
    private RemindManager remindManager;
    private RemindTraceNickchangeHandler remindNickChangeTracer;
    private JoinPartListener deliverRemindHandler;
    //private OnAnyActionListener deliverOnActionHandler;

    

    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
            DuplicatedSignatureException {
        super(myPolly);
        
        this.logger = Logger.getLogger(myPolly.getLoggerName(this.getClass()));
        
        myPolly.persistence().registerEntity(RemindEntity.class);
        
        this.remindManager = new RemindManager(myPolly);
        this.deliverRemindHandler = new DeliverRemindHandler(this.remindManager);
        //this.deliverOnActionHandler = new DeliverOnActionListener(this.remindManager);
        
        this.remindNickChangeTracer = new RemindTraceNickchangeHandler(
                this.remindManager);
        myPolly.irc().addNickChangeListener(this.remindNickChangeTracer);
        myPolly.irc().addJoinPartListener(this.deliverRemindHandler);
        //myPolly.irc().addJoinPartListener(this.deliverOnActionHandler);
        //myPolly.irc().addMessageListener(this.deliverOnActionHandler);
        
        this.addDisposable(this.remindManager);
        
        this.addCommand(new LeaveCommand(myPolly, this.remindManager));
        //this.addCommand(new OnReturnCommand(myPolly, this.remindManager));
        this.addCommand(new RemindCommand(myPolly, this.remindManager));
        this.addCommand(new MyRemindsCommand(myPolly, this.remindManager));
        this.addCommand(new DeleteRemindCommand(myPolly, this.remindManager));
        this.addCommand(new SleepCommand(myPolly, this.remindManager));
        this.addCommand(new RemindSettingsCommand(myPolly));
    }
    
    
    
    @Override
    public void actualDispose() throws DisposingException {
        this.getMyPolly().irc().removeNickChangeListener(this.remindNickChangeTracer);
        this.getMyPolly().irc().removeJoinPartListener(this.deliverRemindHandler);
        //this.getMyPolly().irc().removeJoinPartListener(this.deliverOnActionHandler);
        //this.getMyPolly().irc().removeMessageListener(this.deliverOnActionHandler);
        super.actualDispose();
    }       

    
    
    @Override
    public void onLoad() {
        logger.info("Scheduling all reminds...");
        List<RemindEntity> reminds = this.remindManager.getAllReminds();
        for (RemindEntity remind : reminds) {
            this.remindManager.scheduleRemind(remind, remind.getDueDate());
        }
        
        try {
            UserManager users = this.getMyPolly().users();
            users.addAttribute(REMIND_FORMAT_NAME, REMIND_FORMAT_VALUE);
            users.addAttribute(MESSAGE_FORMAT_NAME, MESSAGE_FORMAT_VALUE);
            users.addAttribute(SLEEP_TIME, SLEEP_DEFAULT_VALUE);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
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
