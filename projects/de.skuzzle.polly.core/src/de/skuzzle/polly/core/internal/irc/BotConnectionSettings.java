package de.skuzzle.polly.core.internal.irc;

import java.util.List;

public class BotConnectionSettings {

    private String nickName;
    private String hostName;
    private List<Integer> ports;
    private String identity;
    private List<String> channels;
    private String modes;
    private int lastPortIdx;
    

    
    public BotConnectionSettings(String nickName, String hostName, 
            List<Integer> ports, String identity, List<String> channels, String modes) {
        this.nickName = nickName;
        this.hostName = hostName;
        this.ports = ports;
        this.identity = identity;
        this.channels = channels;
        this.modes = modes;
        this.lastPortIdx = 0;
    }
    
    
    
    public String getNickName() {
        return this.nickName;
    }



    public String getHostName() {
        return this.hostName;
    }



    public int getPort() {
        return this.ports.get(this.lastPortIdx++ % this.ports.size());
    }



    public String getIdentity() {
        return this.identity;
    }    
    
    
    
    public List<String> getChannels() {
        return this.channels;
    }
    
    
    
    public String getModes() {
        return this.modes;
    }
}
