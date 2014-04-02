package polly.mud.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.EventProviders;


public class MudTCPConnection implements Closeable {
        

    public static MudTCPConnection connect(String host, int port, 
            ConnectionListener callback) throws UnknownHostException, IOException {
        
        final MudTCPConnection result = new MudTCPConnection(host, port);
        if (callback != null) {
            result.addConnectionListener(callback);
        }
        result.connect();
        return result;
    }
    
    
    
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Thread receiver;
    private final EventProvider events = EventProviders.newDefaultEventProvider();
    private final String host;
    private final int port;
    
    
    private MudTCPConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    
    
    public synchronized void connect() throws IOException {
        if (this.socket != null && this.socket.isConnected()) {
            return;
        }
        
        this.socket = new Socket(this.host, this.port);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.receiver = new Thread("RECEIVER") {
            
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    try {
                        final String line = in.readLine();
                        if (line == null) {
                            break;
                        }
                        fireMessageReceived(line);
                    } catch (IOException e) {
                        break;
                    }
                }
                
                fireDiconnected();
            }
        };
        this.receiver.start();
        this.fireConnected();
    }
    
    
    
    public void send(String message) {
        try {
            this.out.write(message);
            this.out.flush();
        } catch (IOException e) {
            this.fireDiconnected();
        }
    }
    
    
    
    public void submit(String message) {
        this.send(message + "\r\n");
    }
    
    
    
    private void fireMessageReceived(final String s) {
        final MudMessageEvent e = new MudMessageEvent(this, s);
        this.events.dispatch(ConnectionListener.class, e, 
                ConnectionListener.RECEIVED);
    }
    
    
    
    private void fireConnected() {
        final MudEvent e = new MudEvent(this);
        this.events.dispatch(ConnectionListener.class, e, 
                ConnectionListener.CONNECTED);
    }
    
    
    
    private void fireDiconnected() {
        final MudEvent e = new MudEvent(this);
        this.events.dispatch(ConnectionListener.class, e, 
                ConnectionListener.DISCONNECTED);
    }
    
    
    
    public void addConnectionListener(ConnectionListener listener) {
        this.events.addListener(ConnectionListener.class, listener);
    }
    
    
    
    public void removeConnectionListener(ConnectionListener listener) {
        this.events.removeListener(ConnectionListener.class, listener);
    }



    @Override
    public void close() throws IOException {
        if (this.receiver != null) {
            this.receiver.interrupt();
        }
        if (this.socket != null) {
            this.socket.close();
        }
        this.in.close();
        this.out.close();
    }
}
