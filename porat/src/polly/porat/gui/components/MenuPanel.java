package polly.porat.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;



public class MenuPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JToolBar content;
    
    public MenuPanel(String caption, Icon icon) {
        super(new BorderLayout(2, 2));
        this.add(new TitleBar(caption, icon), BorderLayout.NORTH);
        this.setPreferredSize(new Dimension(150, 100));
        this.content = new JToolBar(JToolBar.VERTICAL);
        this.content.setFloatable(false);
        this.content.setRollover(true);
        this.content.setLayout(new BoxLayout(this.content, BoxLayout.Y_AXIS));
        this.content.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.add(this.content, BorderLayout.CENTER);
    }
    
    
    
    public void addComponent(JButton comp) {
        int width = this.getPreferredSize().width;
        comp.setAlignmentX(0.0f);
        comp.setMaximumSize(new Dimension(width, 30));
        comp.setPreferredSize(new Dimension(width, 30));
        comp.setMinimumSize(new Dimension(width, 30));
        comp.setFocusPainted(false);
        comp.setBorderPainted(false);
        comp.setVisible(true);
        this.content.add(comp);
    }
    
    
    
    public void addSeparator() {
        this.content.add(Box.createVerticalStrut(5));
    }

}
