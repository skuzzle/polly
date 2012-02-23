package polly.porat.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import polly.porat.gui.util.CustomFocusTraversalPolicy;



public class OkCancelBar extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private KeyButton okButton;
    private KeyButton cancelButton;
    private ActionListener okAction;
    private ActionListener cancelAction;
    
    
    private ActionListener nop = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {}
    };
    
    
    
    public OkCancelBar() {
        super(new BorderLayout());
        this.okAction = this.nop;
        this.cancelAction = this.nop;
        this.createComponents();
    }
    
    
    
    public OkCancelBar(ActionListener okAction, 
            ActionListener cancelAction) {
        super(new BorderLayout());
        this.okAction = okAction;
        this.cancelAction = cancelAction;
        this.createComponents();
    }
    
    
    
    public KeyButton getOkButton() {
        return this.okButton;
    }
    
    
    
    public KeyButton getCancelButton() {
        return this.cancelButton;
    }
    
    
    
    public void setOkAction(ActionListener okAction) {
        this.okButton.removeActionListener(this.okAction);
        this.okAction = okAction;
        this.okButton.addActionListener(this.okAction);
    }
    
    
    
    public ActionListener getOkAction() {
        return this.okAction;
    }
    
    
    
    public void setCancelAction(ActionListener cancelAction) {
        this.cancelButton.removeActionListener(this.cancelAction);
        this.cancelAction = cancelAction;
        this.cancelButton.addActionListener(this.cancelAction);
    }
    
    
    public ActionListener getCancelAction() {
        return this.cancelAction;
    }
    
    
    
    public boolean isOk(Object source) {
        return this.okButton == source;
    }
    
    
    
    public boolean isCancel(Object source) {
        return this.cancelButton == source;
    }
    
    
    
    private void createComponents() {
        JPanel content = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        Dimension buttonSize = new Dimension(90, 26);
        
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        
        this.okButton = new KeyButton("Ok", enter);
        this.okButton.setPreferredSize(buttonSize);
        this.okButton.setMnemonic(KeyEvent.VK_O);
        this.okButton.addActionListener(this.okAction);
       
        this.cancelButton = new KeyButton("Cancel", escape);       
        this.cancelButton.setPreferredSize(buttonSize);
        this.cancelButton.setMnemonic(KeyEvent.VK_A);
        this.cancelButton.addActionListener(this.cancelAction);
        
        
        this.add(sep, BorderLayout.NORTH);
        content.add(this.cancelButton);
        content.add(this.okButton);
        this.add(content, BorderLayout.SOUTH);
        
        this.setFocusTraversalPolicyProvider(true);
        Vector<Component> rightOrder = new Vector<Component>();
        rightOrder.add(this.okButton);
        rightOrder.add(this.cancelButton);
        this.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(rightOrder));
    }
}
