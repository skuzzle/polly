package http;

import java.util.Map;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import core.RemindManager;
import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.annotations.Param;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpResourceAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.DateType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTools;
import de.skuzzle.polly.sdk.time.Milliseconds;
import entities.RemindEntity;


public class RemindHttpController extends PollyController {
    
    private final static String FILES = "/http/view/files"; //$NON-NLS-1$
    
    private final static String REMIND_CATEGORY_KEY = "httpRemindCategory"; //$NON-NLS-1$
    private final static String PAGE_REMINDS_DESC_KEY = "httpRemindMngrDesc"; //$NON-NLS-1$
    private final static String PAGE_REMINDS_NAME_KEY = "httpRemindMngrName"; //$NON-NLS-1$
    
    public final static String PAGE_REMINDS = "/pages/remindOverview"; //$NON-NLS-1$
    private final static String PAGE_REMINDS_CONTENT = "http/view/remind.overview.html"; //$NON-NLS-1$

    public static final String API_CANCEL_REMIND = "/api/cancelRemind"; //$NON-NLS-1$
    public static final String API_DISCARD_SNOOZE = "/api/discardSnooze"; //$NON-NLS-1$
    public static final String API_TOGGLE_REMIND = "/api/toggleRemind"; //$NON-NLS-1$
    public static final String API_SET_SNOOZE = "/api/setSnooze"; //$NON-NLS-1$
    public static final String API_MODIFY_REMIND = "/api/modifyRemind"; //$NON-NLS-1$
    

    
    private final RemindManager rm;
    
    public RemindHttpController(MyPolly myPolly, RemindManager rm) {
        super(myPolly);
        this.rm = rm;
    }

    
    
    @Override
    protected Controller createInstance() {
        return new RemindHttpController(this.getMyPolly(), this.rm);
    }
    
    
    
    @Override
    protected Map<String, Object> createContext(String content) {
        final Map<String, Object> c = super.createContext(content);
        HTMLTools.gainFieldAccess(c, MSG.class, "MSG"); //$NON-NLS-1$
        return c;
    }
    
    
    
    @Get(FILES)
    public HttpAnswer getFile() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        final ClassLoader cl = this.getClass().getClassLoader();
        return new HttpResourceAnswer(200, cl, this.getEvent().getPlainUri());
    }

    
    
    @Get(value = PAGE_REMINDS, name = PAGE_REMINDS_NAME_KEY)
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY,
        MSG.FAMILY,
        REMIND_CATEGORY_KEY,
        PAGE_REMINDS_DESC_KEY,
        MyPlugin.REMIND_PERMISSION
    })
    public HttpAnswer remindOverview() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        final Map<String, Object> c = this.createContext(PAGE_REMINDS_CONTENT);
        
        final RemindEntity snoozable = this.rm.getSnoozabledRemind(
            this.getSessionUser().getName());
        
        // remind run time in seconds
        final long rt;
        if (snoozable != null) {
            rt = Milliseconds.toSeconds(
                snoozable.getDueDate().getTime() - snoozable.getLeaveDate().getTime());
        } else {
            rt = 0;
        }

        c.put("runtime", this.getMyPolly().formatting().formatTimeSpan(rt)); //$NON-NLS-1$
        c.put("snoozable", snoozable); //$NON-NLS-1$
        c.put("lastRemind", this.rm.getLastRemind(this.getSessionUser())); //$NON-NLS-1$
        c.put("defaultRemindTime", this.getSessionUser().getAttribute( //$NON-NLS-1$
            MyPlugin.DEFAULT_REMIND_TIME).valueString(this.getMyPolly().formatting()));
        return this.makeAnswer(c);
    }
    
    
    
    @Get(API_CANCEL_REMIND)
    public HttpAnswer cancelRemind(@Param("remindId") int id) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        try {
            
            this.rm.deleteRemind(this.getSessionUser(), id);
            return new GsonHttpAnswer(200, 
                new SuccessResult(true, MSG.httpRemindMngrCancelSuccess));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.httpRemindMngrDatabaseFail));
        } catch (CommandException e) {
            return new GsonHttpAnswer(200, e.getMessage());
        }
    }
    
    
    
    @Get(API_DISCARD_SNOOZE)
    public HttpAnswer discardSnooze() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        this.rm.cancelSleep(this.getSessionUser().getName());
        return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
    }
    
    
    
    public static class ToggleRemindResult extends SuccessResult {
        public final boolean isMail;
        public ToggleRemindResult(boolean isMail) {
            super(true, ""); //$NON-NLS-1$
            this.isMail = isMail;
        }    
    }
    
    
    
    @Get(API_TOGGLE_REMIND)
    public HttpAnswer toggleRemind(@Param("remindId") int id) 
            throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        try {
            final RemindEntity re = this.rm.toggleIsMail(getSessionUser(), id);
            return new GsonHttpAnswer(200, new ToggleRemindResult(re.isMail()));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.httpRemindMngrDatabaseFail));
        } catch (CommandException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, e.getMessage()));
        }
    }
    
    
    
    @Get(API_SET_SNOOZE)
    public HttpAnswer setSnooze(
        @Param(value = "timespan", treatEmpty = true, ifEmptyValue = "") String exp) 
                throws AlternativeAnswerException {
        
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        final Types parsed = this.getMyPolly().parse(exp);
        if (!(parsed instanceof DateType)) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, MSG.httpRemindMngrNoValidDate));
        }
        final DateType target = (DateType) parsed;
        try {
            this.rm.snooze(this.getSessionUser(), target.getValue());
            return new GsonHttpAnswer(200, new SuccessResult(true, "")); //$NON-NLS-1$
        } catch (CommandException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpRemindMngrDatabaseFail));
        }
    }
    
    
    
    public static class ModifyRemindResult extends SuccessResult {
        public final String dueDate;
        public final String remindMessage;
        
        
        public ModifyRemindResult(String dueDate, String remindMessage) {
            super(true, ""); //$NON-NLS-1$
            this.dueDate = dueDate;
            this.remindMessage = remindMessage;
        }
    }
    
    @Get(API_MODIFY_REMIND)
    public HttpAnswer modifyRemind(
        @Param("remindId") int id, 
        @Param(value = "message", treatEmpty = true) String message, 
        @Param(value = "dueDate", treatEmpty =  true) String dueDate) 
                throws AlternativeAnswerException {
        
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        Types dd = this.getMyPolly().parse(dueDate);
        if (!(dd instanceof DateType)) {
            // invalid date submitted, do not change
            dd = null;
        }
        if (message.equals("")) { //$NON-NLS-1$
            message = null; // no message submitted, do not change
        }
        
        try {
            final RemindEntity re = this.rm.modifyRemind(
                this.getSessionUser(), 
                id, 
                dd == null ? null : ((DateType) dd).getValue(), 
                message);
            
            return new GsonHttpAnswer(200, 
                new ModifyRemindResult(
                    this.getMyPolly().formatting().formatDate(re.getDueDate()), message));
        } catch (CommandException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                    new SuccessResult(false, MSG.httpRemindMngrDatabaseFail));
        }
    }
}
