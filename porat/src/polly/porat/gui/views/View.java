package polly.porat.gui.views;

import javax.swing.Icon;
import javax.swing.JComponent;


public interface View {

    public abstract String getName();
    
    public abstract Icon getIcon();
    
    public abstract JComponent getContent();
    
    public abstract void onSwitchTo(View fromView);
    
    public abstract void onSwitchAway(View nextView);
}