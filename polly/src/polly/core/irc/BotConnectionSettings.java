package polly.core.irc;

import java.util.List;

public class BotConnectionSettings {

    private String nickName;
    private String hostName;
    private List<Integer> ports;
    private String identity;
    private List<String> channels;
    private String modes;
    

    public BotConnectionSettings(String nickName, String hostName, 
            List<Integer> ports, String identity, List<String> channels, String modes) {
        this.nickName = nickName;
        this.hostName = hostName;
        this.ports = ports;
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
        int rndIdx = (int) (this.ports.size() * Math.random());
        return this.ports.get(rndIdx);
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
