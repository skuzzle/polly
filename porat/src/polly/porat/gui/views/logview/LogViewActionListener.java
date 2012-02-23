package polly.porat.gui.views.logview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import polly.porat.logic.GuiController;



public class LogViewActionListener implements ActionListener {
    
    private JCheckBox liveLog;
    private JButton update;
    private JComboBox<File> files;
    private GuiController guiController;
    private JTextField filter;
    private TableRowSorter<LogItemTableModel> rowFilter;
    private LogItemTableModel model;
    
    public LogViewActionListener(JCheckBox liveLog, 
            JButton update, 
            JTextField filter, 
            TableRowSorter<LogItemTableModel> rowFilter,  
            JComboBox<File> files,
            LogItemTableModel model,
            GuiController guiController) {
        
        this.liveLog = liveLog;
        this.update = update;
        this.filter = filter;
        this.rowFilter = rowFilter;
        this.files = files;
        this.model = model;
        this.guiController = guiController;
        
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.liveLog) {
            this.model.getData().clear();
            this.model.fireTableDataChanged();
            this.guiController.toggleLiveLog(this.liveLog.isSelected());
            this.files.setEnabled(!this.liveLog.isSelected());
        } else if (e.getSource() == this.update) {
            this.guiController.requestUpdate(this.liveLog.isSelected());
        } else if (e.getSource() == this.filter) {
            if (this.filter.getText().equals("")) {
                this.rowFilter.setRowFilter(null);
                return;
            }
            
            // case insensitive pattern!
            String filterPattern = "(?i).*" + this.filter.getText() +".*";
            this.rowFilter.setRowFilter(RowFilter.regexFilter(filterPattern));
        }
    }
}
