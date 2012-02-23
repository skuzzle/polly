package polly.porat.gui.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import polly.porat.gui.images.Icons;


public class ConnectPanel extends JToolBar implements DocumentListener, ActionListener {

    private static final long serialVersionUID = 1L;

    
    private JTextField hostText;
    private JTextField portText;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private KeyButton connect;
    private KeyButton disconnect;
    private String defaultHost;
    private int defaultPort;
    
    
    public ConnectPanel(String defaultHost, int defaultPort) {
        this.defaultHost = defaultHost;
        this.defaultPort = defaultPort;
        this.setFloatable(false);
        this.setRollover(true);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.createContent();
    }
    
    
    
    private void createContent() {
        this.connect = new KeyButton("Connect", Icons.CONNECTED_ICON);
        this.connect.setKeyStroke(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, KeyEvent.CTRL_MASK));
        this.connect.setEnabled(false);
        
        this.disconnect = new KeyButton("Disconnect", Icons.DISCONNECTED_ICON);
        this.disconnect.setKeyStroke(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, KeyEvent.CTRL_MASK));
        this.disconnect.setVisible(false);
        
        this.hostText = new JTextField();
        this.hostText.setText(this.defaultHost);
        this.hostText.addActionListener(this);
        this.hostText.getDocument().addDocumentListener(this);
        
        this.portText = new JTextField();
        this.portText.setText("" + this.defaultPort);
        this.portText.getDocument().addDocumentListener(this);
        this.portText.addActionListener(this);
        
        this.userNameText = new JTextField();
        this.userNameText.getDocument().addDocumentListener(this);
        this.userNameText.addActionListener(this);
        
        this.passwordText = new JPasswordField();
        this.passwordText.getDocument().addDocumentListener(this);
        this.passwordText.addActionListener(this);
        
        JLabel hostLabel = new JLabel("Host:  ");
        hostLabel.setDisplayedMnemonic('H');
        hostLabel.setLabelFor(this.hostText);
        
        JLabel portLabel = new JLabel("Port:  ");
        portLabel.setDisplayedMnemonic('P');
        portLabel.setLabelFor(this.portText);
        
        JLabel userNameLabel = new JLabel("Username:  ");
        userNameLabel.setDisplayedMnemonic('U');
        userNameLabel.setLabelFor(this.userNameText);
        
        JLabel passwordLabel = new JLabel("Password:  ");
        passwordLabel.setDisplayedMnemonic('a');
        passwordLabel.setLabelFor(this.passwordText);
        
        this.add(Box.createHorizontalStrut(6));
        this.add(hostLabel);
        this.add(this.hostText);
        this.add(Box.createHorizontalStrut(6));
        this.add(portLabel);
        this.add(this.portText);
        this.add(Box.createHorizontalStrut(6));
        this.add(userNameLabel);
        this.add(this.userNameText);
        this.add(Box.createHorizontalStrut(6));
        this.add(passwordLabel);
        this.add(this.passwordText);
        this.add(Box.createHorizontalStrut(6));
        this.add(this.connect);
        this.add(this.disconnect);
    }
    
    

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.hostText.setEnabled(enabled);
        this.portText.setEnabled(enabled);
        this.userNameText.setEnabled(enabled);
        this.passwordText.setEnabled(enabled);
        this.connect.setEnabled(enabled);
    }
    
    
    
    public void setConnected() {
        this.setEnabled(false);
        this.connect.setVisible(false);
        this.disconnect.setVisible(true);
    }
    
    
    
    public void setDisconnected() {
        this.setEnabled(true);
        this.disconnect.setVisible(false);
        this.connect.setVisible(true);
    }
    
    
    
    public JTextField getHostText() {
        return this.hostText;
    }
    
    
    
    public JTextField getPortText() {
        return this.portText;
    }
    
    
    
    public JTextField getUserNameText() {
        return this.userNameText;
    }
    
    
    
    public JPasswordField getPasswordText() {
        return this.passwordText;
    }
    
    
    
    public KeyButton getConnect() {
        return this.connect;
    }
    
    
    
    
    public KeyButton getDisconnect() {
        return this.disconnect;
    }



    @Override
    public void changedUpdate(DocumentEvent e) {}



    @Override
    public void insertUpdate(DocumentEvent e) {
        this.toggleConnectEnableState();
    }



    @Override
    public void removeUpdate(DocumentEvent e) {
        this.toggleConnectEnableState();
    }
    
    
    
    private void toggleConnectEnableState() {
        this.connect.setEnabled(
            !this.hostText.getText().equals("") &&
            !this.portText.getText().equals("") &&
            !this.userNameText.getText().equals("") &&
            !(this.passwordText.getPassword().length == 0));
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        this.connect.doClick();
    }
}
