package polly.porat.gui.views.logview;

import java.util.List;


import polly.network.protocol.Constants;
import polly.network.protocol.Constants.ResponseType;
import polly.network.protocol.LogItem;
import polly.porat.events.ProtocolEvent;
import polly.porat.events.ProtocolListener;


public class IncomingLogHandler implements ProtocolListener {

    private LogItemTableModel model;
    
    
    
    public IncomingLogHandler(LogItemTableModel model) {
        this.model = model;
    }
    
    
    
    @SuppressWarnings("unchecked")
    @Override
    public void responseReceived(ProtocolEvent e) {
        if (e.getResponse().getType() == ResponseType.LOG_ITEM) {
            List<LogItem> logs = (List<LogItem>) 
                    e.getResponse().getPayload().get(Constants.LOG_LIST);
            
            this.model.getData().addAll(logs);
            //this.model.fireTableRowsInserted(row, this.model.getRowCount());
            this.model.fireTableDataChanged();
        }
    }
    
    

    @Override
    public void errorReceived(ProtocolEvent e) {
    }

}
