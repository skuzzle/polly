package core.filters;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
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
        return this.myPolly.irc().isOnChannel(log.getChannel(), 
            this.executer.getCurrentNickName()) || log.getType() == LogEntry.TYPE_UNKNOWN;
    }

}
