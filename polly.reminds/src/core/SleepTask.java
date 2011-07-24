package core;

import java.util.TimerTask;


public class SleepTask extends TimerTask {

    private String nickName;
    private RemindManager remindManager;
    
    public SleepTask(String nickName, RemindManager remindManager) {
        this.nickName = nickName;
        this.remindManager = remindManager;
    }
    
    
    
    @Override
    public void run() {
        this.remindManager.removeSleep(this.nickName);
    }

}