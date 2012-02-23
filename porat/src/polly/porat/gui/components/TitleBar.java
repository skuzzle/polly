package polly.porat.gui.components;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToolBar;



public class TitleBar extends JToolBar implements MouseListener {

    private static final long serialVersionUID = 1L;
    
    private static final Color MOUSE_OVER = new Color(214, 239, 252);

    private JLabel titleLabel;
    
    
    public TitleBar(String title, Icon icon) {
        this.setFloatable(false);
        this.setRollover(true);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.setBackground(TitleBar.MOUSE_OVER);
        
        this.add(Box.createRigidArea(new Dimension(5, 25)));
        
        this.titleLabel = new JLabel(title, icon, JLabel.LEADING);
        this.add(this.titleLabel);
        
        //this.add(Box.createHorizontalGlue());
    }
    
    
    
    public void setIcon(Icon icon) {
        this.titleLabel.setIcon(icon);
    }
    
    
    
    public void setText(String text) {
        this.titleLabel.setText(text);
    }
    
    
    
    @Override
    public void mouseEntered(MouseEvent e) {
        this.setBackground(TitleBar.MOUSE_OVER);
    }


    @Override
    public void mouseExited(MouseEvent e) {
        this.setBackground(SystemColor.info);
    }
    
    


    @Override
    public void mouseClicked(MouseEvent ignore) {}



    @Override
    public void mousePressed(MouseEvent ignore) {}

    

    @Override
    public void mouseReleased(MouseEvent ignore) {}
}
