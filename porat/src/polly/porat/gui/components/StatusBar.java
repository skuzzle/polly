package polly.porat.gui.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;

import polly.porat.gui.images.Icons;


public class StatusBar extends JToolBar {


    private static final long serialVersionUID = 1L;

    
    private Icon[] icons = {Icons.PING_0_ICON, Icons.PING_1_ICON, 
        Icons.PING_2_ICON, Icons.PING_3_ICON, 
        Icons.PING_4_ICON, Icons.PING_5_ICON};


    //private int[] thresholds = {200, 150, 100, 50, 25, 0};
    private int[] thresholds = {200, 4, 3, 2, 1, 0};
    
    
    private JLabel status;
    private JProgressBar progress;
    private JLabel activity;
    private JLabel ping;
    private boolean isOnline;
    
    
    public StatusBar() {
        super(JToolBar.HORIZONTAL);
        this.setPreferredSize(new Dimension(100, 28));
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.status = new JLabel();
        
        this.progress = new JProgressBar();
        this.progress.setIndeterminate(true);
        this.progress.setVisible(false);
        this.activity = new JLabel("Activity: ");
        this.activity.setVisible(false);
        
        this.ping = new JLabel("Ping: 0 ms");
        this.ping.setVisible(false);
        
        this.setOnline(false);
        
        this.add(Box.createHorizontalStrut(10));
        this.add(this.status);
        this.add(Box.createHorizontalGlue());
        this.add(this.activity);
        this.add(this.progress);
        this.add(Box.createHorizontalGlue());
        this.add(this.ping);
        this.add(Box.createHorizontalStrut(10));
    }
    
    
    
    
    public void setOnline(boolean value) {
        this.isOnline = value;
        this.ping.setVisible(value);
        if (!value) {
            this.status.setText("Offline");
            this.status.setIcon(Icons.CROSS_ICON);
        } else {
            this.status.setText("Connected");
            this.status.setIcon(Icons.PING_5_ICON);
        }
    }
    
    
    
    public synchronized void setActivity(String activity, Icon icon) {
        this.activity.setText(activity);
        this.activity.setVisible(true);
        this.activity.setIcon(icon);
        this.progress.setVisible(true);
    }
    
    
    
    public synchronized void endActivity() {
        this.progress.setVisible(false);
        this.activity.setVisible(false);
    }
    
    
    
    private void setStatusIcon(int latency) {
        if (!this.isOnline) {
            this.status.setIcon(Icons.CROSS_ICON);
            return;
        }
        
        // if connected, get icon dependent on current latency
        for (int i = 0; i < this.thresholds.length; ++i) {
            if (latency >= thresholds[i]) {
                this.status.setIcon(this.icons[i]);
                return;
            }
        }
        throw new RuntimeException("should not happen: setStatusIcon");
    }
    
    
    
    public void updatePing(int latency) {
        setStatusIcon(latency);
        ping.setText("Ping: " + latency + " ms");
    }
}
