package polly.porat.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;


import polly.network.Connection;
import polly.network.events.NetworkEvent;
import polly.network.events.ObjectReceivedEvent;
import polly.network.protocol.Constants;
import polly.network.protocol.ErrorResponse;
import polly.network.protocol.Ping;
import polly.network.protocol.Pong;
import polly.network.protocol.ProtocolObject;
import polly.network.protocol.Response;
import polly.network.protocol.Constants.ResponseType;



class ClientConnection implements Connection, Runnable {
    
    private final static Logger logger = Logger.getLogger(
            ClientConnection.class.getName());
    
    private int id;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ExecutorService connectionThread;
    private AtomicBoolean shutdownFlag;
    private Ping lastPing;
    private int latency;
    private ClientProtocolHandler handler;
    
    
    
    public ClientConnection(InetAddress serverHost, int port, 
            ClientProtocolHandler handler) throws IOException {
        
        this.shutdownFlag = new AtomicBoolean(false);
        this.connectionThread = Executors.newSingleThreadExecutor();
        this.handler = handler;
        
        try {
            SSLContext context = SSLContext.getDefault();
            SocketFactory sf = context.getSocketFactory();
        
            this.socket = sf.createSocket(serverHost, port);
            this.input = new ObjectInputStream(this.socket.getInputStream());
            this.output = new ObjectOutputStream(this.socket.getOutputStream());
        
            Object in = null;
            try {
                in = this.input.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
    
            if (!(in instanceof Response)) {
                throw new IOException("invalid server answer");
            }
            Response response = (Response) in;
            if (response.is(ResponseType.ACCEPTED)) {
                
                this.lastPing = new Ping();
                this.lastPing.setReceivedAt(System.currentTimeMillis());
                
                this.id = (Integer) response.getPayload().get(Constants.CONNECTION_ID);
                this.connectionThread.execute(this);
            } else if (response.is(ResponseType.ERROR)) {
                throw new IOException("connection rejected. Error code: " + 
                    ((ErrorResponse) response).getErrorType());
            }
        } catch (NoSuchAlgorithmException e) {
            this.close();
            throw new IOException(e);
        } catch (IOException e) {
            this.close();
            throw e;
        }
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    @Override
    public void send(ProtocolObject message) {
        if (this.shutdownFlag.get() || this.output == null) {
            return;
            // throw new IOException("Trying to send over closed connection");
        }
        
        try {
            synchronized (this.output) {
                message.setTimestamp(System.currentTimeMillis());
                this.output.writeObject(message);
                this.output.flush();
                this.output.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error while sending", e);
            this.close();
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
                    
                    if (po instanceof Ping) {
                        Ping ping = (Ping) po;
                        
                        long ct = po.getReceivedAt() - this.lastPing.getReceivedAt();
                        long st = po.getTimestamp() - this.lastPing.getTimestamp();
                        
                        this.latency = (int) Math.abs(ct - st);
                        this.lastPing = ping;
                        
                        this.send(new Pong());
                    }
                    this.handler.objectReceived(new ObjectReceivedEvent(this, po));
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("Received invalid class", e);
        } catch (IOException e) {
            if (!this.shutdownFlag.get()) {
                logger.error("IO Error", e);
            }
            
        } finally {
            this.close();
        }
    }
    
    
    
    public int latency() {
        return this.latency;
    }
    
    
    
    @Override
    public boolean isConnected() {
        return !this.shutdownFlag.get() && this.socket.isConnected();
    }
    
    

    @Override
    public boolean isAuthenticated() {
        return false;
    }
    
    
    
    public void close() {
        if (this.shutdownFlag.get()) {
            return;
        }
        
        this.shutdownFlag.set(true);
        try {
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            logger.error("Error while closing socket", e);
        }
        
        this.connectionThread.shutdown();
        this.handler.connectionClosed(new NetworkEvent(this));
    }

}
