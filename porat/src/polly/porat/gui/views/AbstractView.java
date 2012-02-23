package polly.porat.gui.views;

import javax.swing.Icon;


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
    
    
    public String getName() {
        return this.name;
    }
    
    
    @Override
    public void onSwitchAway(View nextView) {}
    
    @Override
    public void onSwitchTo(View fromView) {}
}
