package http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import core.PollyLoggingManager;
import core.filters.DateLogFilter;
import core.filters.LogFilter;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.roles.RoleManager;
import entities.LogEntry;


public class ReplayTableModel extends LogEntryTableModel {

    public ReplayTableModel(PollyLoggingManager lm, MyPolly myPolly) {
        super(lm, myPolly);
    }
    
    
    
    @Override
    public List<LogEntry> getData(HttpEvent e) {
        final User u = (User) e.getSession().getAttached("user");

        if (!this.myPolly.users().isSignedOn(u)) {
            return Collections.emptyList();
        }

        final LogFilter dateFilter = new DateLogFilter(new Date(u.getLastIdleTime()));
        final List<LogEntry> all = new ArrayList<>();
        
        for (final String channel : this.myPolly.irc().getChannels()) {
            if (this.myPolly.irc().isOnChannel(channel, u.getCurrentNickName())
                || this.myPolly.roles()
                    .hasPermission(u, RoleManager.ADMIN_PERMISSION)) {

                try {
                    List<LogEntry> logs = this.lm.preFilterChannel(channel);
                    logs = this.lm.postFilter(logs, dateFilter);
                    all.addAll(logs);
                } catch (DatabaseException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return all;
    }
}
