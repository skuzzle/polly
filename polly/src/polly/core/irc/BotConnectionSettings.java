package polly.core.irc;

import java.util.List;

public class BotConnectionSettings {

    private String nickName;
    private String hostName;
    private int port;
    private String identity;
    private List<String> channels;
    private String modes;
    

    public BotConnectionSettings(String nickName, String hostName, 
            int port, String identity, List<String> channels, String modes) {
        this.nickName = nickName;
        this.hostName = hostName;
        this.port = port;
        this.identity = identity;
        this.channels = channels;
        this.modes = modes;
    }
    
    
    
    public String getNickName() {
        return this.nickName;
    }



    public String getHostName() {
        return this.hostName;
    }



    public int getPort() {
        return this.port;
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
