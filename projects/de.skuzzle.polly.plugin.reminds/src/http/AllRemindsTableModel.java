package http;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import core.RemindManager;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.roles.RoleManager;
import entities.RemindEntity;


public class AllRemindsTableModel extends MyRemindTableModel {

    public AllRemindsTableModel(RemindManager rm) {
        super(rm);
    }
    
    
    
    @Override
    public Set<String> getRequiredPermission() {
        return Collections.singleton(RoleManager.ADMIN_PERMISSION);
    }

    
    
    
    @Override
    public List<RemindEntity> getData(HttpEvent e) {
        return this.rm.getDatabaseWrapper().getAllReminds();
    }
}
