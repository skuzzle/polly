package core;



import java.util.TimerTask;

import org.apache.log4j.Logger;


import entities.RemindEntity;

public class RemindTask extends TimerTask {

    private RemindEntity remind;
    private RemindManagerImpl remindManagerImpl;
    private Logger logger;
    
    public RemindTask(RemindEntity remind, RemindManagerImpl remindManagerImpl, Logger logger) {
        this.remind = remind;
        this.remindManagerImpl = remindManagerImpl;
        this.logger = logger;
    }
    
    
    
    @Override
    public void run() {
        try {
            this.remindManagerImpl.deliverRemind(this.remind);
        } catch (Exception e) {
            logger.error("Error while delivering remind", e);
        }
    }
    
}
