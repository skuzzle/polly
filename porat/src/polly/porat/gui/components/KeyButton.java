package polly.porat.gui.components;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.KeyStroke;


public class KeyButton extends JButton implements PropertyChangeListener {
    
    
    private class PerformClickAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        
        private JButton button;
        
        public PerformClickAction(JButton button) {
            this.button = button;
        }
        

        @Override
        public void actionPerformed(ActionEvent e) {
            this.button.doClick();
        }        
    }
    
    
    private static final long serialVersionUID = 1L;
    
    private KeyStroke stroke;

    
    
    public KeyButton() {
        super();
    }
    
    
    
    public KeyButton(KeyStroke stroke) {
        super();
        this.setKeyStroke(stroke);
    }
    
    

    public KeyButton(Action action) {
        super(action);
        action.addPropertyChangeListener(this);
    }
    
    

    public KeyButton(Icon icon) {
        super(icon);
    }
    
    

    public KeyButton(String title, Icon icon) {
        super(title, icon);
    }
    
    

    public KeyButton(String title) {
        super(title);
    }
    
    
    
    public KeyButton(String title, KeyStroke stroke) {
        super(title);
        this.setKeyStroke(stroke);
    }
    
    

    public void setKeyStroke(KeyStroke stroke) {
        this.stroke = stroke;
        this.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke, "enter");
        this.getActionMap().put("enter", new PerformClickAction(this));
    }

    
    
    public KeyStroke getKeyStroke() {
        return this.stroke;
    }

    
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        KeyStroke stroke = (KeyStroke) this.getAction().getValue(Action.ACCELERATOR_KEY);
        this.setKeyStroke(stroke);
    }

}
