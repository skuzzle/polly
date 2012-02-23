package polly.porat.gui.views;

import javax.swing.JComponent;
import javax.swing.JPanel;

import polly.porat.gui.images.Icons;


public class IRCView extends AbstractView {

    public IRCView() {
        super("IRC Viewer", Icons.IRC_VIEW_ICON);
    }

    
    @Override
    public JComponent getContent() {
        return new JPanel();
    }
}
