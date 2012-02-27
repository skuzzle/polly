package polly.porat.logic;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import polly.network.events.ConnectionListener;
import polly.network.events.NetworkEvent;
import polly.network.protocol.Constants;
import polly.network.protocol.ErrorResponse;
import polly.network.protocol.Response;
import polly.network.util.SerializableFile;
import polly.porat.events.DefaultEventProvider;
import polly.porat.events.Dispatchable;
import polly.porat.events.EventProvider;
import polly.porat.events.FilesReceivedEvent;
import polly.porat.events.FilesReceivedListener;
import polly.porat.events.PingListener;
import polly.porat.events.ProtocolEvent;
import polly.porat.events.ProtocolListener;
import polly.porat.gui.MainWindow;
import polly.porat.gui.images.Icons;
import polly.porat.gui.util.SwingInvoke;
import polly.porat.gui.views.View;
import polly.porat.network.ClientProtocolHandler;



public class GuiController implements ProtocolListener {

    private final static Logger logger = Logger.getLogger(GuiController.class.getName());

    private ClientProtocolHandler networkHandler;
    private MainWindow mainWindow;
    private EventProvider eventProvider;
    
    
    
    public GuiController(MainWindow mainWindow) {
        this.eventProvider = new DefaultEventProvider(
            Executors.newSingleThreadExecutor());
        this.mainWindow = mainWindow;
        this.networkHandler = new ClientProtocolHandler(this.eventProvider);
        this.networkHandler.addProtocolListener(this);
        
        this.networkHandler.addConnectionListener(new ConnectionListener() {
            
            @Override
            public void connectionClosed(NetworkEvent e) {
                setOnline(false);
            }
            
            
            
            @Override
            public void connectionAccepted(NetworkEvent e) {}
        });
        
        
        
        this.networkHandler.addPingListener(new PingListener() {
            
            @Override
            public void ping(final int latency) {
                SwingInvoke.later(new Runnable() {
                    @Override
                    public void run() {
                        GuiController.this.mainWindow.getStatusBar().updatePing(latency);
                    }
                });
            }
        });
    }
    
    
    
    public void setActivity(final String activity, final Icon icon) {
        SwingInvoke.andWait(new Runnable() {
            
            @Override
            public void run() {
                mainWindow.getStatusBar().setActivity(activity, icon);
            }
        });
    }
    
    
    
    public void endActivity() {
        SwingInvoke.andWait(new Runnable() {
            
            @Override
            public void run() {
                mainWindow.getStatusBar().endActivity();
            }
        });
    }
    
    
    
    public void setOnline(final boolean value) {
        SwingInvoke.later(new Runnable() {
            
            @Override
            public void run() {
                if (value) {
                    mainWindow.getConnectPanel().setConnected();
                } else {
                    mainWindow.getConnectPanel().setDisconnected();
                }
                mainWindow.getStatusBar().setOnline(value);
                mainWindow.getStatusBar().endActivity();
            }
        });
    }
    
    
    
    public void addView(View view) {
        this.mainWindow.addView(view);
        this.networkHandler.addConnectionListener(view);
    }
    
    
    
    public void connect(InetAddress host, int port, String userName, String pw) 
            throws IOException {

        logger.info("Connecting to " + host + ":" + port + " as " + userName);
        this.setActivity("Connecting...  ", Icons.NETWORKING_ICON);
        this.networkHandler.connect(host, port, userName, pw);
    }
    
    
    
    public void requestUpdate(boolean cacheOnly) {
        logger.info("Requesting updates");
        this.setActivity("Downloading logfiles...  ", Icons.NETWORKING_ICON);
        this.networkHandler.requestUpdates(cacheOnly);
    }
    
    
    
    public void toggleLiveLog(boolean value) {
        this.setActivity("Waiting for response...  ", Icons.NETWORKING_ICON);
        if (value) {
            this.networkHandler.enableLiveLog();
        } else {
            this.networkHandler.disableLiveLog();
        }
    }
    
    
    
    public ClientProtocolHandler getNetworkHandler() {
        return this.networkHandler;
    }
    
    
    
    private void storeFiles(Response response) {
        @SuppressWarnings("unchecked")
        List<SerializableFile> logList = (List<SerializableFile>) 
                response.getPayload().get(Constants.LOG_LIST);
        
        final List<File> stored = new ArrayList<File>(logList.size());
        for (SerializableFile file : logList) {
            try {
                File f = new File("./cache/", file.getName());
                file.store(f);
                stored.add(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        this.fireFilesReceived(new FilesReceivedEvent(this, stored));
    }
    
    
    
    public void addFilesReceivedListener(FilesReceivedListener listener) {
        this.eventProvider.addListener(FilesReceivedListener.class, listener);
    }
    
    
    
    public void removeFilesReceivedListener(FilesReceivedListener listener) {
        this.eventProvider.removeListener(FilesReceivedListener.class, listener);
    }
    
    
    
    private void fireFilesReceived(final FilesReceivedEvent e) {
        List<FilesReceivedListener> listeners = 
            this.eventProvider.getListeners(FilesReceivedListener.class);
        
        Dispatchable<FilesReceivedListener, FilesReceivedEvent> event = 
            new Dispatchable<FilesReceivedListener, FilesReceivedEvent>(listeners, e) {

                @Override
                public void dispatch(FilesReceivedListener listener,
                    FilesReceivedEvent event) {
                    listener.filesReceived(e);
                }
        };
        this.eventProvider.dispatchEvent(event);
    }


    
    @Override
    public void responseReceived(ProtocolEvent e) {
        switch (e.getType()) {
        case LOGGED_IN: this.setOnline(true); break;
        case FILE: this.storeFiles(e.getResponse());
        case LIVE_LOG_ON: this.endActivity(); break;
        case LIVE_LOG_OFF: this.endActivity(); break;
        case UPDATE_DONE: this.endActivity(); break;
        }
    }



    @Override
    public void errorReceived(ProtocolEvent e) {
        ErrorResponse error = (ErrorResponse) e.getResponse();
        switch (error.getErrorType()) {
        case INVALID_PASSWORD:
        case UNKNOWN_USER:
            this.networkHandler.disconnect();
            JOptionPane.showMessageDialog(this.mainWindow, 
                "Invalid password or username.", "Login Error", JOptionPane.ERROR_MESSAGE);
            break;
        case INSUFFICIENT_RIGHTS:
            this.networkHandler.disconnect();
            JOptionPane.showMessageDialog(this.mainWindow, 
                "This user has insufficient rights.", "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            break;
        }
    }

}
