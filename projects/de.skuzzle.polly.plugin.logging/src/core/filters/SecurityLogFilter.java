package core.filters;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.roles.RoleManager;
import entities.LogEntry;


public class SecurityLogFilter implements LogFilter {

    private MyPolly myPolly;
    private User executer;
    
    public SecurityLogFilter(MyPolly myPolly, User executer) {
        this.myPolly = myPolly;
        this.executer = executer;
    }
    
    
    
    @Override
    public boolean accept(LogEntry log) {
        if (this.myPolly.roles().hasPermission(this.executer, RoleManager.ADMIN_PERMISSION)) {
            return true;
        }
        return this.myPolly.irc().isOnChannel(log.getChannel(), 
            this.executer.getCurrentNickName()) || log.getType() == LogEntry.TYPE_UNKNOWN;
    }

}
