package core;

import java.util.TimerTask;


public class SleepTask extends TimerTask {

    private String nickName;
    private RemindManagerImpl remindManagerImpl;
    
    public SleepTask(String nickName, RemindManagerImpl remindManagerImpl) {
        this.nickName = nickName;
        this.remindManagerImpl = remindManagerImpl;
    }
    
    
    
    @Override
    public void run() {
        this.remindManagerImpl.removeSleep(this.nickName);
    }

}