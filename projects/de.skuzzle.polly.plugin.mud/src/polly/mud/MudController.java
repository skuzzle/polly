package polly.mud;

import polly.mud.connection.ConnectionListener;
import polly.mud.connection.MudEvent;
import polly.mud.connection.MudMessageEvent;
import polly.mud.connection.MudTCPConnection;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;


public class MudController implements ConnectionListener {
    
    public synchronized static MudController getInstance() {
        if (instance == null) {
            throw new RuntimeException("not initialized");
        }
        return instance;
    }
    
    
    static synchronized void create(MyPolly myPolly, String nickName) {
        if (instance != null) {
            throw new RuntimeException("only one instance allowed");
        }
        instance = new MudController(myPolly, nickName);
    }
    
    
    private static MudController instance;
    
    private final String nickName;
    private final MyPolly myPolly;
    private String forward;
    private MessageListener forwardListener;
    private MudTCPConnection connection;
    
    
    
    private MudController(MyPolly myPolly, String nickName)  {
        this.myPolly = myPolly;
        this.nickName = nickName;
    }
    
    
    
    public synchronized void activateForward(final String channel) {
        this.forward = channel;
        if (this.forwardListener != null) {
            this.myPolly.irc().removeMessageListener(this.forwardListener);
        }
        this.forwardListener = new MessageAdapter() {
            private final String PREFIX = "mud ";
            
            private synchronized void onMessage(MessageEvent e) {
                if (e.getMessage().startsWith(PREFIX)) {
                    final User user = myPolly.users().getUser(e.getUser());
                    
                    if (user == null || !myPolly.roles().hasPermission(
                                user, MyPlugin.MUD_PERMISSION)) {
                        return;
                    }
                    
                    final String message = e.getMessage().substring(PREFIX.length());
                    if (connection != null) {
                        connection.submit(message);
                    }
                }
            }
            
            @Override
            public void privateMessage(MessageEvent e) {
                this.onMessage(e);
            }
            
            
            
            @Override
            public void publicMessage(MessageEvent e) {
                this.onMessage(e);
            }
        };
        this.myPolly.irc().addMessageListener(this.forwardListener);
    }
    
    

    @Override
    public synchronized void received(MudMessageEvent e) {
        System.out.println(e.getMessage());
        if (e.getMessage().equals("By what name do you wish to be hailed?")) {
            System.out.println("Sending nick name to server...");
            e.getSource().submit(this.nickName);
        }
        
        if (this.forward != null && !e.getMessage().startsWith(">")) {
            this.myPolly.irc().sendMessage(this.forward, e.getMessage(), this);
        }
        
        final String SAYS_PREFIX = " says '";
        final int i = e.getMessage().indexOf(SAYS_PREFIX);
        if (i != -1) {
            final String message = e.getMessage().substring(
                    i + SAYS_PREFIX.length(), e.getMessage().length() - 1); // strip trailing '
            System.out.println("'" + message + "'");
            final Types t = this.myPolly.parse(message);
            if (t != null && !(t instanceof StringType)) {
                e.getSource().submit("say " + t.valueString(this.myPolly.formatting()));
            }
        }
    }

    
    
    @Override
    public synchronized void connected(MudEvent e) {
        System.out.println("Connected");
        this.connection = e.getSource();
    }

    
    
    @Override
    public synchronized void disconnected(MudEvent e) {
        System.out.println("Disconnected");
        if (this.forwardListener != null) {
            this.myPolly.irc().removeMessageListener(this.forwardListener);
        }
    }
}
