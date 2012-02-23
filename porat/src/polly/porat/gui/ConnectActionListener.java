package polly.porat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import polly.porat.gui.components.ConnectPanel;
import polly.porat.logic.GuiController;


public class ConnectActionListener implements ActionListener {

    private ConnectPanel connectPanel;
    private GuiController guiController;
    
    
    
    public ConnectActionListener(GuiController guiController, ConnectPanel connectPanel) {
        this.guiController = guiController;
        this.connectPanel = connectPanel;
    }


    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.connectPanel.getDisconnect())) {
            this.guiController.getNetworkHandler().disconnect();
            return;
        }
        
        String host = this.connectPanel.getHostText().getText();
        int port = Integer.parseInt(this.connectPanel.getPortText().getText());
        String userName = this.connectPanel.getUserNameText().getText();
        String password = new String(this.connectPanel.getPasswordText().getPassword());
        
        
        try {
            this.connectPanel.setEnabled(false);
            this.guiController.connect(InetAddress.getByName(host), port, 
                userName, password);
        } catch (UnknownHostException e1) {
            JOptionPane.showMessageDialog(this.connectPanel, 
                "Unknown host: " + e1.getMessage(), 
                "Connect Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(this.connectPanel, 
                "IO Error: " + e1.getMessage(), 
                "Connect Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
}