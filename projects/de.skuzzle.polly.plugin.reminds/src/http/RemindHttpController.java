package http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import de.skuzzle.polly.sdk.roles.RoleManager;
import entities.RemindEntity;


public class RemindHttpController extends PollyController {

    private final RemindManager rm;
    
    public RemindHttpController(MyPolly myPolly, RemindManager rm) {
        super(myPolly);
        this.rm = rm;
    }

    
    
    @Override
    protected Controller createInstance() {
        return new RemindHttpController(this.getMyPolly(), this.rm);
    }
    
    
    
    @Get("/http/view/files")
    public HttpAnswer getFile() {
        final ClassLoader cl = this.getClass().getClassLoader();
        return new HttpResourceAnswer(200, cl, this.getEvent().getPlainUri());
    }

    
    
    @Get(value = "/pages/remindOverview", name = "Overview")
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY,
        "Reminds",
        "List, modify, add and delete your reminds",
        MyPlugin.REMIND_PERMISSION
    })
    public HttpAnswer remindOverview() throws AlternativeAnswerException {
        this.requirePermissions(MyPlugin.REMIND_PERMISSION);
        final Map<String, Object> c = this.createContext("http/view/remind.overview.html");
        
        final List<RemindEntity> userReminds = 
            this.rm.getDatabaseWrapper().getMyRemindsForUser(
                this.getSessionUser().getName());
        
        Collections.sort(userReminds, RemindEntity.BY_DUE_DATE);
        c.put("userReminds", userReminds);

        final List<RemindEntity> allReminds;
        if (this.getMyPolly().roles().hasPermission(this.getSessionUser(), 
                RoleManager.ADMIN_PERMISSION)) {
            allReminds = this.rm.getDatabaseWrapper().getAllReminds();
        } else {
            allReminds = new ArrayList<>();
        }
        Collections.sort(allReminds, RemindEntity.BY_DUE_DATE);
        c.put("allReminds", allReminds);
        c.put("snoozable", this.rm.getSnoozabledRemind(this.getSessionUser().getName()));
        c.put("lastRemind", this.rm.getLastRemind(this.getSessionUser()));
        c.put("defaultRemindTime", this.getSessionUser().getAttribute(
            MyPlugin.DEFAULT_REMIND_TIME).valueString(this.getMyPolly().formatting()));
        return this.makeAnswer(c);
    }
    
    
    
    @Get("/api/cancelRemind")
    public HttpAnswer cancelRemind(@Param("remindId") int id) {
        try {
            this.rm.deleteRemind(id);
            return new GsonHttpAnswer(200, 
                new SuccessResult(true, "Remind has been deleted"));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, "Database exception while deleting remind"));
        }
    }
    
    
    
    @Get("/api/discardSnooze")
    public HttpAnswer discardSnooze() {
        this.rm.cancelSleep(this.getSessionUser().getName());
        return new GsonHttpAnswer(200, new SuccessResult(true, ""));
    }
    
    
    
    public static class ToggleRemindResult extends SuccessResult {
        public final boolean isMail;
        public ToggleRemindResult(boolean isMail) {
            super(true, "");
            this.isMail = isMail;
        }    
    }
    
    
    
    @Get("/api/toggleRemind")
    public HttpAnswer toggleRemind(@Param("remindId") int id) {
        try {
            final RemindEntity re = this.rm.toggleIsMail(getSessionUser(), id);
            return new GsonHttpAnswer(200, new ToggleRemindResult(re.isMail()));
        } catch (DatabaseException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, "Database exception while toggling remind"));
        } catch (CommandException e) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, e.getMessage()));
        }
    }
    
    
    
    @Get("/api/setSnooze")
    public HttpAnswer setSnooze(
        @Param(value = "timespan", treatEmpty = true, ifEmptyValue = "") String expression) {
        final Types parsed = this.getMyPolly().parse(expression);
        if (!(parsed instanceof DateType)) {
            return new GsonHttpAnswer(200, 
                new SuccessResult(false, "Input yielded no valid date"));
        }
        final DateType target = (DateType) parsed;
        try {
            this.rm.snooze(this.getSessionUser(), target.getValue());
            return new GsonHttpAnswer(200, new SuccessResult(true, ""));
        } catch (CommandException | DatabaseException e) {
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
    }
    
    
    
    public static class ModifyRemindResult extends SuccessResult {
        public final String dueDate;
        public final String remindMessage;
        
        
        public ModifyRemindResult(String dueDate, String remindMessage) {
            super(true, "");
            this.dueDate = dueDate;
            this.remindMessage = remindMessage;
        }
    }
    
    @Get("/api/modifyRemind")
    public HttpAnswer modifyRemind(
        @Param("remindId") int id, 
        @Param(value = "message", treatEmpty = true) String message, 
        @Param(value = "dueDate", treatEmpty =  true) String dueDate) {
        
        Types dd = this.getMyPolly().parse(dueDate);
        if (!(dd instanceof DateType)) {
            // invalid date submitted, do not change
            dd = null;
        }
        if (message.equals("")) {
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
            return new GsonHttpAnswer(200, new SuccessResult(false, e.getMessage()));
        }
    }
}
