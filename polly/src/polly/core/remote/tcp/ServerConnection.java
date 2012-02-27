package polly.core.remote.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import polly.network.events.NetworkEvent;
import polly.network.events.ObjectReceivedEvent;
import polly.network.protocol.Constants.ErrorType;
import polly.network.protocol.ErrorResponse;
import polly.network.protocol.Ping;
import polly.network.protocol.Pong;
import polly.network.protocol.ProtocolObject;
import polly.util.concurrent.ThreadFactoryBuilder;

import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.model.User;



public class ServerConnection implements Runnable, Disposable, polly.network.Connection {

    private static Logger logger = Logger.getLogger(ServerConnection.class.getName());
    
    private int id;
    private Socket socket;
    private AdministrationServer server;
    private AtomicBoolean shutdownFlag;
    private AtomicBoolean pongReceived;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private int badRequests;
    private int maxBadRequests;
    private User user;
    private ScheduledExecutorService pingService;
    private int loginTimeOut;
    private long start;
    private ReentrantLock userLock;
    private boolean ignorePing;
    
    
    public ServerConnection(int id, AdministrationServer server, Socket socket, 
            int maxBadRequests, int loginTimeOut) throws IOException {
        
        this.id = id;
        this.server = server;
        this.socket = socket;
        this.maxBadRequests = maxBadRequests;
        this.pongReceived = new AtomicBoolean(true);
        this.shutdownFlag = new AtomicBoolean(false);
        this.start = System.currentTimeMillis();
        this.loginTimeOut = loginTimeOut;
        this.userLock = new ReentrantLock();
        
        logger.trace("Creating streams from socket");
        
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        
        this.schedulePingService();
    }
    
    
    
    private void schedulePingService() {
        Runnable r = new Runnable() {
            
            @Override
            public void run() {

                if (shutdownFlag.get()) {
                    return;
                }
                
                // close this connection if no login happened within reasonable time
                if (!isAuthenticated() && 
                        System.currentTimeMillis() - start > loginTimeOut) {
                    logger.info("No login Request within reasonable time from " + 
                        ServerConnection.this);
                    send(new ErrorResponse(ErrorType.LOGIN_TIMEOUT));
                    dispose();
                }
            
            
                if (ignorePing) {
                    pongReceived.set(false);
                } else if (pongReceived.get()) {
                    pongReceived.set(false);
                    send(new Ping());
                } else {
                    logger.error("Ping timeout: " + ServerConnection.this);
                    dispose();
                } 
            }
        };
        
        this.pingService = Executors.newScheduledThreadPool(1, 
            new ThreadFactoryBuilder("PING_SERVICE_" + this.id));
        this.pingService.scheduleAtFixedRate(r, 5000, 5000, TimeUnit.MILLISECONDS);
    }
    
    
    
    public void send(ProtocolObject message) {
        this.send(message, false);
    }

    
    
    public void send(ProtocolObject message, boolean ignorePing) {
        if (this.shutdownFlag.get() || this.output == null) {
            return;
            // throw new IOException("Trying to send over closed connection");
        }
        
        try {
            synchronized (this.output) {
                if (ignorePing) {
                    logger.warn("Ignoring pings for " + this);
                }
                this.ignorePing = ignorePing;
                message.setTimestamp(System.currentTimeMillis());
                this.output.writeObject(message);
                this.output.flush();
                this.output.reset();
                this.ignorePing = false;
            }
        } catch (IOException e) {
            if (!this.isDisposed()) { 
                logger.error("Error while sending", e);
            }
            this.dispose();
        }
    }
    
    
    
    
    public boolean isIgnorePing() {
        return this.ignorePing;
    }
    
    
    
    public void setIgnorePing(boolean ignorePing) {
        this.ignorePing = ignorePing;
    }
    
    
    
    public boolean isAuthenticated() {
        return this.getUser() != null;
    }
    
    
    
    public User getUser() {
        try {
            this.userLock.lock();
            return this.user;
        } finally {
            this.userLock.unlock();
        }
    }
    
    
    
    public void setUser(User user) {
        try {
            this.userLock.lock();
            this.user = user;
        } finally {
            this.userLock.unlock();
        }
        
    }
    
    
    
    @Override
    public void run() {
        try {
            while (!this.shutdownFlag.get()) {
                Object incoming = this.input.readObject();
                
                if (incoming instanceof ProtocolObject) {
                    ProtocolObject po = (ProtocolObject) incoming;
                    po.setReceivedAt(System.currentTimeMillis());
                    
                    // reply to ping
                    if (po instanceof Pong) {
                        this.pongReceived.set(true);
                    } else {
                        this.server.fireObjectReceived(
                            new ObjectReceivedEvent(this, po));
                    }
                } else {
                    this.badRequests++;
                    
                    if (this.badRequests > this.maxBadRequests) {
                        logger.warn("Too many bad requests from " + this);
                        this.send(new ErrorResponse(ErrorType.BAD_REQUESTS));
                        this.dispose();
                    }
                }
            }
            logger.info(this + " shut down properly");
        } catch (IOException e) {
            if (this.isDisposed() || !this.isAuthenticated()) {
                // its ok, we are already shutting down
            } else {
                logger.error("IO Error in " + this, e);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Received invalid class", e);
        } finally {
            this.dispose();
        }
    }
    
    
    
    @Override
    public String toString() {
        return "Connection ID " + this.id + " (" + this.socket.getInetAddress() + ")";
    }



    @Override
    public boolean isDisposed() {
        return this.shutdownFlag.get();
    }



    @Override
    public void dispose() {
        if (this.shutdownFlag.get()) {
            return;
        }
        this.shutdownFlag.set(true);
        
        logger.info("Closing " + this);
        synchronized (this.server.getConnections()) {
            this.server.getConnections().remove(this);
        }
        
        this.pingService.shutdown();
        
        
        try {
            this.socket.close();
        } catch (IOException e) {
            logger.error("Excpetion while closing", e);
        }
        
        this.server.fireConnectionClosed(new NetworkEvent(this));
    }



    @Override
    public boolean isConnected() {
        return !this.shutdownFlag.get() && this.socket.isConnected();
    }



    public Object getId() {
        return this.id;
    }
}
