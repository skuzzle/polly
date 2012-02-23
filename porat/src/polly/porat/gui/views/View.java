package polly.porat.gui.views;

import javax.swing.Icon;
import javax.swing.JComponent;

import polly.network.events.ConnectionListener;


public interface View extends ConnectionListener {

    public abstract String getName();
    
    public abstract Icon getIcon();
    
    public abstract void setEnabled(boolean value);
    
    public abstract JComponent getContent();
    
    public abstract void onSwitchTo(View fromView);
    
    public abstract void onSwitchAway(View nextView);
}