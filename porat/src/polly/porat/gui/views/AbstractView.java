package polly.porat.gui.views;

import javax.swing.Icon;

import polly.network.events.NetworkEvent;


public abstract class AbstractView implements View {
    
    private String name;
    private Icon icon;

    
    public AbstractView(String name, Icon icon) {
        this.name = name;
        this.icon = icon;
    }
    
    
    @Override
    public Icon getIcon() {
        return this.icon;
    }
    
    
    
    @Override
    public String getName() {
        return this.name;
    }
    
    
    @Override
    public void setEnabled(boolean value) {}
    
    
    @Override
    public void connectionAccepted(NetworkEvent e) {
        this.setEnabled(true);
    }
    
    
    @Override
    public void connectionClosed(NetworkEvent e) {
        this.setEnabled(false);
    }
    
    
    @Override
    public void onSwitchAway(View nextView) {}
    
    @Override
    public void onSwitchTo(View fromView) {}
}
