package polly.porat.gui.views.logview;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingWorker;

import polly.network.protocol.LogItem;
import polly.porat.events.FilesReceivedEvent;
import polly.porat.events.FilesReceivedListener;
import polly.porat.gui.images.Icons;
import polly.porat.gui.util.SwingInvoke;
import polly.porat.logic.GuiController;
import polly.porat.util.LogFileParser;


public class LogFileComboboxHandler implements ItemListener, FilesReceivedListener {
    
    private JComboBox<File> logFileCombo;
    private LogItemTableModel model;
    private GuiController guiController;
    
    
    public LogFileComboboxHandler(GuiController guiController, 
            JComboBox<File> logFileCombo, LogItemTableModel model) {
        this.guiController = guiController;
        this.logFileCombo = logFileCombo;
        this.model = model;
    }
    
    
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        this.logFileCombo.setEnabled(false);
        final File file = (File) e.getItem();
        
        SwingWorker<List<LogItem>, Void> worker = new SwingWorker<List<LogItem>, Void>() {

            @Override
            protected List<LogItem> doInBackground() throws Exception {
                guiController.setActivity("Parsing Logfile...", Icons.DRIVE_ICON);
                List<LogItem> logItems = LogFileParser.parseLogFile(file);
                
                return logItems;
            }
            
            
            
            @Override
            protected void done() {
                try {
                    List<LogItem> logItems = this.get();
                    model.setData(logItems);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    guiController.endActivity();
                    logFileCombo.setEnabled(true);
                }
            }
            
        };
        worker.execute();
    }



    @Override
    public void filesReceived(final FilesReceivedEvent e) {
        SwingInvoke.later(new Runnable() {
            
            @Override
            public void run() {
                File[] files = new File[e.getFiles().size()];
                e.getFiles().toArray(files);
                
                logFileCombo.setModel(
                    new DefaultComboBoxModel<File>(files));
            }
        });
        
    }

}
