package polly.porat.gui.views.logview;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.TableRowSorter;

import polly.porat.gui.components.KeyButton;
import polly.porat.gui.images.Icons;
import polly.porat.gui.views.AbstractView;


public class LogView extends AbstractView {
    
    private final static String TABLE_VIEW = "table";
    private final static String TEXT_VIEW = "text";

    private JPanel viewContainer;
    private JPanel content;
    private JComboBox<File> logFileCombo;
    private JCheckBox liveLog;
    private KeyButton update;
    private JTextField filter;
    private JTable logTable;
    private JTextArea logText;
    private TableRowSorter<LogItemTableModel> rowFilter;
    private LogItemTableModel model;

    

    public LogView() {
        super("Log Viewer", Icons.LOG_VIEW_ICON);
        this.createContent();
        this.setEnabled(false);
    }

 
    
    @Override
    public void setEnabled(boolean value) {
        this.update.setEnabled(value);
        this.liveLog.setEnabled(value);
        this.logFileCombo.setEnabled(value);
        this.filter.setEnabled(value);
    }
    
    
    
    @Override
    public JComponent getContent() {
        return this.content;
    }
    
    
    
    private JPanel createContent() {
        this.content = new JPanel(new BorderLayout(2, 2));
        this.content.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        
        this.logFileCombo = new JComboBox<File>();
        JLabel logComboLabel = new JLabel("Select a Logfile:  ");
        logComboLabel.setLabelFor(logFileCombo);
        this.liveLog = new JCheckBox("Live Log");
        this.update = new KeyButton("Update", 
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        
        top.add(Box.createHorizontalStrut(6));
        top.add(logComboLabel);
        top.add(this.logFileCombo);
        top.add(this.liveLog);
        top.add(this.update);
        top.add(Box.createHorizontalStrut(6));
        
        this.viewContainer = new JPanel(new CardLayout());
        this.logTable = new JTable();
        this.logTable.setShowGrid(false);
        this.model = new LogItemTableModel();
        this.logTable.setModel(this.model);
        this.rowFilter = new TableRowSorter<LogItemTableModel>(this.model);
        this.logTable.setRowSorter(this.rowFilter);
        
        this.logText = new JTextArea();
        this.viewContainer.add(new JScrollPane(this.logTable), TABLE_VIEW);
        this.viewContainer.add(new JScrollPane(this.logText), TEXT_VIEW);
        this.content.add(this.viewContainer);
        
        
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        
        this.filter = new JTextField();
        
        JLabel filterLabel = new JLabel("Filter:  ");
        filterLabel.setLabelFor(this.filter);
        bottom.add(Box.createHorizontalStrut(6));
        bottom.add(filterLabel);
        bottom.add(this.filter);
        
        this.content.add(top, BorderLayout.NORTH);
        this.content.add(bottom, BorderLayout.SOUTH);
        
        return this.content;
    }
    
    
    
    public LogItemTableModel getModel() {
        return this.model;
    }
    
    
    
    public JButton getUpdate() {
        return this.update;
    }
    
    
    
    public JCheckBox getLiveLog() {
        return this.liveLog;
    }
    
    
    
    public JTextField getFilter() {
        return this.filter;
    }
    
    
    
    public JComboBox<File> getLogFileCombo() {
        return this.logFileCombo;
    }
    
    
    
    public TableRowSorter<LogItemTableModel> getRowFilter() {
        return this.rowFilter;
    }
    
}
