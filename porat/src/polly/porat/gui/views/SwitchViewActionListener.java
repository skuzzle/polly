package polly.porat.gui.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import polly.porat.gui.MainWindow;


public class SwitchViewActionListener implements ActionListener {

    private MainWindow mainWindow;
    private String viewName;
    
    
    
    public SwitchViewActionListener(MainWindow mainWindow, String viewName) {
        this.mainWindow = mainWindow;
        this.viewName = viewName;
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        this.mainWindow.switchView(this.viewName);
    }
    
}
