package polly.porat.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

import polly.porat.gui.components.OkCancelBar;
import polly.porat.gui.components.TitleBar;


public abstract class Dialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    
    public final static int CANCEL = 0;
    public final static int OK = 1;

    private TitleBar titleBar;
    private OkCancelBar okCancelBar;
    private int result = CANCEL;
    
    
    
    public Dialog(Window owner, String title, String subTitle, Icon icon) {
        super(owner);
        this.titleBar = new TitleBar(subTitle, icon);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setModal(true);
        this.setIconImage(((ImageIcon) icon).getImage());
        this.setTitle(title);
        this.okCancelBar = new OkCancelBar(this, this);
        this.add(this.okCancelBar, BorderLayout.SOUTH);
        
        
        this.add(this.titleBar, BorderLayout.NORTH);
    }
    
    
    
    public abstract boolean onOk();
    
    public abstract boolean onCancel();
    
    
    
    public int showDialog() {
        this.pack();
        this.setVisible(true);
        return this.result;
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean close = true;
        
        if (this.okCancelBar.isOk(e.getSource())) {
            close = this.onOk();
            this.result = OK;
        } else if (this.okCancelBar.isCancel(e.getSource())) {
            close = this.onCancel();
            this.result = CANCEL;
        }
        
        if (close) {
            this.setVisible(false);
            this.dispose();
        }
    }
    
    
    
    public void setSubTitle(String title) {
        this.titleBar.setText(title);
    }
    
    
    
    @Override
    public void setIconImage(Image image) {
        super.setIconImage(image);
        this.titleBar.setIcon(new ImageIcon(image));
    }
    
}
