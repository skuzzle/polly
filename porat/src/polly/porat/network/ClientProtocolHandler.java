package polly.porat.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import polly.network.events.ConnectionListener;
import polly.network.events.NetworkEvent;
import polly.network.events.ObjectReceivedEvent;
import polly.network.protocol.Constants;
import polly.network.protocol.ErrorResponse;
import polly.network.protocol.Ping;
import polly.network.protocol.Request;
import polly.network.protocol.Response;
import polly.network.protocol.Constants.RequestType;
import polly.porat.events.Dispatchable;
import polly.porat.events.EventProvider;
import polly.porat.events.PingListener;
import polly.porat.events.ProtocolEvent;
import polly.porat.events.ProtocolListener;


public class ClientProtocolHandler {
    
    private final static Logger logger = Logger.getLogger(
            ClientProtocolHandler.class.getName());
    
    

    private ClientConnection connection;
    private ReentrantLock connectionLock;
    private EventProvider eventProvider;
    
    
    
    public ClientProtocolHandler(EventProvider eventProvider) {
        this.connectionLock = new ReentrantLock();
        this.eventProvider = eventProvider;
    }
    
    
    
    public Request connect(InetAddress host, int port, String userName, String password) 
            throws IOException {
        
        try {
            this.connectionLock.lock();
            
            if (this.connection != null) {
                throw new IllegalStateException("connection already active");
            }
            
            this.connection = new ClientConnection(host, port, this);
            
            Request login = new Request(RequestType.LOGIN);
            login.getPayload().put(Constants.USER_NAME, userName);
            login.getPayload().put(Constants.PASSWORD, password);
            this.connection.send(login);
            return login;
        } finally {
            this.connectionLock.unlock();
        }
    }
    
    
    
    public void disconnect() {
        try {
            this.connectionLock.lock();
            
            if (this.connection == null) {
                return;
            }
            
            Request request = new Request(RequestType.LOGOUT);
            this.connection.send(request);
            
            this.connection.close();
            this.connection = null;
        } finally {
            this.connectionLock.unlock();
        }
    }
    
    
    
    public boolean isConnected() {
        return this.connection != null && this.connection.isConnected();
    }
    
    
    
    public Request enableLiveLog() {
        if (!this.isConnected()) {
            return null;
        }
        Request r = new Request(RequestType.LIVE_LOG_ON);
        this.connection.send(r);
        return r;
    }
    
    
    
    public Request disableLiveLog() {
        if (!this.isConnected()) {
            return null;
        }
        Request r = new Request(RequestType.LIVE_LOG_OFF);
        this.connection.send(r);
        return r;
    }
    
    
    
    public Request enableIrcForwad() {
        if (!this.isConnected()) {
            return null;
        }
        Request r = new Request(RequestType.IRC_FORWARD_ON);
        this.connection.send(r);
        return r;
    }
    
    
    
    public Request disableIrcForward() {
        if (!this.isConnected()) {
            return null;
        }
        Request r = new Request(RequestType.IRC_FORWARD_OFF);
        this.connection.send(r);
        return r;
    }
    
    
    
    public Request requestUpdates(boolean cacheOnly) {
        if (!this.isConnected()) {
            return null;
        }
        Request r = null;
        if (cacheOnly) {
            r = new Request(RequestType.UPDATE_CACHE);
        } else {
            r = new Request(RequestType.UPDATE);
        }
        this.connection.send(r);
        return r;
    }
    
    
    
    void fireConnectionAccepted(final NetworkEvent e) {
        List<ConnectionListener> listeners = 
            this.eventProvider.getListeners(ConnectionListener.class);
        
        Dispatchable<ConnectionListener, NetworkEvent> event = 
            new Dispatchable<ConnectionListener, NetworkEvent>(listeners, e) {

                @Override
                public void dispatch(ConnectionListener listener,
                    NetworkEvent event) {
                    listener.connectionAccepted(e);
                }
        };
        this.eventProvider.dispatchEvent(event);
    }
    
    
    
    private void fireConnectionClosed(final NetworkEvent e) {
        List<ConnectionListener> listeners = 
            this.eventProvider.getListeners(ConnectionListener.class);
        
        Dispatchable<ConnectionListener, NetworkEvent> event = 
            new Dispatchable<ConnectionListener, NetworkEvent>(listeners, e) {

                @Override
                public void dispatch(ConnectionListener listener,
                    NetworkEvent event) {
                    listener.connectionClosed(e);
                }
        };
        this.eventProvider.dispatchEvent(event);
    }
    
    
    
    private void fireResponseReceived(final ProtocolEvent e) {
        List<ProtocolListener> listeners = 
            this.eventProvider.getListeners(ProtocolListener.class);
        
        Dispatchable<ProtocolListener, ProtocolEvent> event = 
            new Dispatchable<ProtocolListener, ProtocolEvent>(listeners, e) {

                @Override
                public void dispatch(ProtocolListener listener,
                    ProtocolEvent event) {
                    listener.responseReceived(e);
                }
        };
        this.eventProvider.dispatchEvent(event);
    }
    
    
    
    private void fireErrorReceived(final ProtocolEvent e) {
        List<ProtocolListener> listeners = 
            this.eventProvider.getListeners(ProtocolListener.class);
        
        Dispatchable<ProtocolListener, ProtocolEvent> event = 
            new Dispatchable<ProtocolListener, ProtocolEvent>(listeners, e) {

                @Override
                public void dispatch(ProtocolListener listener,
                    ProtocolEvent event) {
                    listener.errorReceived(e);
                }
        };
        this.eventProvider.dispatchEvent(event);
    }
    
    
    
    private void firePingReceived(final int latency) {
        List<PingListener> listeners = 
            this.eventProvider.getListeners(PingListener.class);
        
        Dispatchable<PingListener, Integer> event = 
            new Dispatchable<PingListener, Integer>(listeners, latency) {

                @Override
                public void dispatch(PingListener listener,
                    Integer latency) {
                    listener.ping(latency);
                }
        };
        this.eventProvider.dispatchEvent(event);
    }
    
    
    
    public void addProtocolListener(ProtocolListener listener) {
        this.eventProvider.addListener(ProtocolListener.class, listener);
    }
    
    
    
    public void removeProtocolListener(ProtocolListener listener) {
        this.eventProvider.removeListener(ProtocolListener.class, listener);
    }
    
    
    
    public void addConnectionListener(ConnectionListener listener) {
        this.eventProvider.addListener(ConnectionListener.class, listener);
    }
    
    
    
    public void removeConnectionListener(ConnectionListener listener) {
        this.eventProvider.addListener(ConnectionListener.class, listener);
    }
    
    
    
    public void addPingListener(PingListener listener) {
        this.eventProvider.addListener(PingListener.class, listener);
    }
    
    
    
    public void removePingListener(PingListener listener) {
        this.eventProvider.removeListener(PingListener.class, listener);
    }

    

    void connectionClosed(NetworkEvent e) {
        this.disconnect();
        this.fireConnectionClosed(e);
    }



    void objectReceived(ObjectReceivedEvent e) {

        if (e.getObject() instanceof Ping) {
            this.firePingReceived(((ClientConnection) e.getSource()).latency());
            return;
        }
        
        if (!(e.getObject() instanceof Response)) {
            logger.error("Received object that is no response: \n" + e.getObject());
            return;
        }
        
        Response response = (Response) e.getObject();
        
        if (response instanceof ErrorResponse) {
            this.fireErrorReceived(new ProtocolEvent(this, response));
        } else {
            this.fireResponseReceived(new ProtocolEvent(this, response));
        }
    }
}