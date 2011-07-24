package polly.core;

public class BotConnectionSettings {

    private String nickName;
    private String hostName;
    private int port;
    private String identity;
    
    

    public BotConnectionSettings(String nickName, String hostName, 
            int port, String identity) {
        this.nickName = nickName;
        this.hostName = hostName;
        this.port = port;
        this.identity = identity;
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
}
