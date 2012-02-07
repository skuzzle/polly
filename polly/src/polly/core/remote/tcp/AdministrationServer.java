package polly.core.remote.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLServerSocketFactory;

import org.apache.log4j.Logger;

import polly.events.DefaultEventProvider;
import polly.events.Dispatchable;
import polly.events.EventProvider;
import polly.network.events.ConnectionListener;
import polly.network.events.NetworkEvent;
import polly.network.events.ObjectReceivedEvent;
import polly.network.events.ObjectReceivedListener;
import polly.network.protocol.Constants.ResponseType;
import polly.network.protocol.ErrorResponse;
import polly.network.protocol.Constants.ErrorType;
import polly.network.protocol.Response;
import polly.util.concurrent.ThreadFactoryBuilder;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.exceptions.DisposingException;


public class AdministrationServer extends AbstractDisposable implements Runnable {
    
    private static Logger logger = Logger.getLogger(AdministrationServer.class.getName());

    private ServerSocket socket;
    private ExecutorService connectionThreadPool;
    private List<ServerConnection> connections;
    private int maxConnections;
    private int maxBadConnections;
    private int ids;
    private AtomicBoolean shutdownFlag;
    private EventProvider eventProvider;
    private Thread serverThread;
    
    
    public AdministrationServer(InetAddress host, int port, int maxConnections) 
            throws IOException {
        this.maxConnections = maxConnections;
        this.shutdownFlag = new AtomicBoolean(false);
        this.connections = new LinkedList<ServerConnection>();
        
        this.connectionThreadPool = Executors.newFixedThreadPool(maxConnections, 
                new ThreadFactoryBuilder("CONNECTION_THREAD_%n%"));
        this.eventProvider = new DefaultEventProvider();
        
        logger.debug("Creating SSL Server Socket");
        SSLServerSocketFactory factory = 
            (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        
        this.socket = factory.createServerSocket(port, 10, host);
    }
    
    
    
    public List<ServerConnection> getConnections() {
        return this.connections;
    }
    
    
    
    public void listen() {
        this.serverThread = new ThreadFactoryBuilder("ADMIN_SERVER").newThread(this);
        this.serverThread.start();
    }
    
    
    
    @Override
    public void run() {
        try {
            while (!this.shutdownFlag.get()) {
                try {
                    logger.info("Waiting for incoming connections...");
                    Socket socket = this.socket.accept();
                    
                    logger.warn("Connection attempt from " + socket.getInetAddress());
                    ServerConnection connection = new ServerConnection(
                        this.ids++, this, socket, this.maxBadConnections, 5000);
    
                    synchronized (this.connections) {
                        if (this.connections.size() >= this.maxConnections) {
                            logger.debug("Connection denied: Connectionlimit reached.");
                            
                            connection.send(new ErrorResponse(ErrorType.LIMIT_EXCEEDED));
                            connection.dispose();
                        } else {
                            connection.send(new Response(ResponseType.ACCEPTED));
                            this.connections.add(connection);
                            this.connectionThreadPool.execute(connection);
                            
                            NetworkEvent e = new NetworkEvent(connection);
                            this.fireConnectionAccepted(e);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while accepting incoming connection", e);
                }
            }
            
            logger.info("Administration server thread shutting down");
        } catch (Exception e) {
            if (this.shutdownFlag.get()) {
                // its ok, we are already shutting down
                return;
            }
            logger.error("Closing administration server", e);
            try {
                this.dispose();
            } catch (DisposingException e1) {
                logger.error("Error while shutting down server", e1);
            }
            
        }
    }
    
    
    
    public void addConnectionListener(ConnectionListener listener) {
        this.eventProvider.addListener(ConnectionListener.class, listener);
    }
    
    
    
    public void removeConnectionListener(ConnectionListener listener) {
        this.eventProvider.removeListener(ConnectionListener.class, listener);
    }
    
    
    
    public void addObjectReceivedListener(ObjectReceivedListener listener) {
        this.eventProvider.addListener(ObjectReceivedListener.class, listener);
    }
    
    
    
    public void removeObjectReceivedListener(ObjectReceivedListener listener) {
        this.eventProvider.removeListener(ObjectReceivedListener.class, listener);
    }
    
    
    
    protected void fireObjectReceived(final ObjectReceivedEvent e) {
        List<ObjectReceivedListener> listeners = 
            this.eventProvider.getListeners(ObjectReceivedListener.class);
        
        Dispatchable<ObjectReceivedListener, ObjectReceivedEvent> event = 
            new Dispatchable<ObjectReceivedListener, ObjectReceivedEvent>(listeners, e) {

                @Override
                public void dispatch(ObjectReceivedListener listener,
                    ObjectReceivedEvent event) {
                    listener.objectReceived(event);
                }
        };
        this.eventProvider.dispatchEvent(event);
    }
    
    
    
    protected void fireConnectionAccepted(final NetworkEvent e) {
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
    
    
    
    protected void fireConnectionClosed(final NetworkEvent e) {
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



    @Override
    protected void actualDispose() throws DisposingException {
        this.shutdownFlag.set(true);
        this.connectionThreadPool.shutdown();
        this.eventProvider.dispose();
    }
}
