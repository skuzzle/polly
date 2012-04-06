package polly.porat.gui.views;

import javax.swing.Icon;
import javax.swing.JComponent;

import polly.network.events.ConnectionListener;
import polly.porat.events.ProtocolListener;


public interface View extends ConnectionListener, ProtocolListener {

    public abstract String getName();
    
    public abstract Icon getIcon();
    
    public abstract void setEnabled(boolean value);
    
    public abstract JComponent getContent();
    
    public abstract void onSwitchTo(View fromView);
    
    public abstract void onSwitchAway(View nextView);
}