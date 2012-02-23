package polly.porat.gui.components;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class LabledComponent extends JPanel {

    private static final long serialVersionUID = 1L;

    private JComponent component;
    
    
    public LabledComponent(String text, JComponent component, Dimension dimension) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JLabel label = new JLabel(text);
        label.setLabelFor(component);
        this.component = component;
        this.add(label);
        this.add(Box.createHorizontalGlue());
        this.add(component);
        this.setAlignmentX(0.0f);
        this.setPreferredSize(dimension);
        System.out.println(dimension);
    }
    
    
    
    public JComponent getLabledComponent() {
        return this.component;
    }
}
