package core;

import java.util.Date;


public interface TVProgramProvider {
    
    public abstract TVProgram getProgram(String channel, Date date);
    
    public abstract TVProgram getCurrent(String channel);
    
    public abstract TVProgram getNext(String channel);
    
    public boolean isChannelSupported(String channel);
}