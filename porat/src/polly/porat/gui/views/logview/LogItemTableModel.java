package polly.porat.gui.views.logview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;


import polly.network.protocol.LogItem;


public class LogItemTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    
    private final static String[] columnNames = {"#", "Type", "Time", "Thread", "Source", "Message"}; 
    private List<LogItem> data;
    

    
    public LogItemTableModel() {
        this.data = new ArrayList<LogItem>();
    }
    
    
    
    public void setData(List<LogItem> data) {
        if (this.data != null) {
            this.data.clear();
        }
        this.data = data;
        this.fireTableDataChanged();
    }
    
    
    public List<LogItem> getData() {
        return this.data;
    }

    
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    

    @Override
    public int getRowCount() {
        return this.data.size();
    }
    
    

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogItem le = this.data.get(rowIndex);
        switch (columnIndex) {
        case 0: return rowIndex;
        case 1: return le.getLevel();
        case 2: return new Date(le.getTimestamp());
        case 3: return le.getThread();
        case 4: return le.getSource();
        case 5: return le.getMessage();
        default: return null;
        }
    }
    
    
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        } else if (columnIndex == 2) {
            return Date.class;
        }
        return super.getColumnClass(columnIndex);
    }

}