package polly.porat;

import javax.swing.UIManager;

import polly.porat.gui.ConnectActionListener;
import polly.porat.gui.MainWindow;
import polly.porat.gui.util.SwingInvoke;
import polly.porat.gui.views.IRCView;
import polly.porat.gui.views.View;
import polly.porat.gui.views.logview.IncomingLogHandler;
import polly.porat.gui.views.logview.LogFileComboboxHandler;
import polly.porat.gui.views.logview.LogView;
import polly.porat.gui.views.logview.LogViewActionListener;
import polly.porat.logic.GuiController;


public class Porat {

    private MainWindow mainWindow;
    private GuiController guiController;
    

    
    public static void main(String[] args) {
        new Porat();
    }
    
    
    
    private Porat() {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            return;
        }
        
        this.initSSL();
        this.mainWindow = new MainWindow();
        this.guiController = new GuiController(this.mainWindow);
        
        
        
        SwingInvoke.later(new Runnable() {
            
            @Override
            public void run() {
                prepareMainWindow(guiController, mainWindow);
                mainWindow.pack();
                mainWindow.setVisible(true);
            }
        });
    }
    
    
    
    private void initSSL() {
        System.setProperty("javax.net.ssl.trustStore", "cfg/SSLKeyStore");
        System.setProperty("javax.net.ssl.trustStorePassword", "blabla");
    }
    
    
    
    private void prepareMainWindow(GuiController guiController, MainWindow mainWindow) {
        // add views
        mainWindow.addView(this.prepareLogView(guiController));
        mainWindow.addView(new IRCView());
        
        // add connect listener
        ConnectActionListener cal = new ConnectActionListener(guiController, 
                mainWindow.getConnectPanel());
        
        mainWindow.getConnectPanel().getConnect().addActionListener(cal);
        mainWindow.getConnectPanel().getDisconnect().addActionListener(cal);
    }
    
    
 
    private View prepareLogView(GuiController guiController) {
        LogView result = new LogView();
        
        LogViewActionListener lva = new LogViewActionListener(
            result.getLiveLog(), 
            result.getUpdate(), 
            result.getFilter(), 
            result.getRowFilter(),
            result.getLogFileCombo(),
            result.getModel(),
            guiController);
        
        result.getUpdate().addActionListener(lva);
        result.getLiveLog().addActionListener(lva);
        result.getFilter().addActionListener(lva);
        
        IncomingLogHandler ilh = new IncomingLogHandler(result.getModel());
        guiController.getNetworkHandler().addProtocolListener(ilh);
        
        
        // Log File combobox related:
        LogFileComboboxHandler lfsl = new LogFileComboboxHandler(
            guiController, result.getLogFileCombo(), result.getModel());
        result.getLogFileCombo().addItemListener(lfsl);
        guiController.addFilesReceivedListener(lfsl);
        return result;
    }
}
