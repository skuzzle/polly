package polly.porat.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import polly.porat.gui.components.ConnectPanel;
import polly.porat.gui.components.MenuPanel;
import polly.porat.gui.components.StatusBar;
import polly.porat.gui.components.TitleBar;
import polly.porat.gui.images.Icons;
import polly.porat.gui.views.SwitchViewActionListener;
import polly.porat.gui.views.View;


public class MainWindow extends JFrame {
    

    private static final long serialVersionUID = 1L;
    private final static String WINDOW_CAPTION = "PoRat";
    
    private MenuPanel menuPanel;
    private StatusBar statusBar;
    private ConnectPanel connectPanel;
    private JPanel viewContainer;
    private TitleBar viewTitle;
    
    private Map<String, View> views;
    private View currentView;
    
    
    
    public MainWindow() {
        super(WINDOW_CAPTION);
        
        this.views = new HashMap<String, View>();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(600, 400));
        this.setContentPane(this.getContent());
    }
    
    
    
    private JPanel getContent() {
        JPanel content = new JPanel(new BorderLayout(2, 2));
        content.add(this.createConnectPanel(), BorderLayout.NORTH);
        content.add(this.createMenuPanel(), BorderLayout.WEST);
        content.add(this.createStatusBar(), BorderLayout.SOUTH);
        content.add(this.createContentPanel(), BorderLayout.CENTER);
        this.revalidate();
        return content;
    }
    
    
    
    private JPanel createContentPanel() {
        JPanel tmp = new JPanel(new BorderLayout(2, 2));
        this.viewTitle = new TitleBar("Nothing to do here", null);
        this.viewContainer = new JPanel(new CardLayout());
        
        tmp.add(this.viewTitle, BorderLayout.NORTH);
        tmp.add(this.viewContainer, BorderLayout.CENTER);
        return tmp;
    }
    
    
    
    private MenuPanel createMenuPanel() {
        this.menuPanel  = new MenuPanel("Views", Icons.TOOLBAR_ICON);
        return this.menuPanel;
    }
    
    
    
    private JToolBar createConnectPanel() {
        this.connectPanel = new ConnectPanel();
        return this.connectPanel;
    }
    
    
    private StatusBar createStatusBar() {
        this.statusBar = new StatusBar();
        return this.statusBar;
    }
    
    
    
    public MenuPanel getMenuPanel() {
        return this.menuPanel;
    }
    
    
    
    public ConnectPanel getConnectPanel() {
        return this.connectPanel;
    }
    
    
    
    public StatusBar getStatusBar() {
        return this.statusBar;
    }
    
    
    
    public void addView(View view) {
        this.viewContainer.add(view.getContent(), view.getName());
        this.views.put(view.getName(), view);
        
        JButton switchView = new JButton(view.getName(), view.getIcon());
        switchView.addActionListener(new SwitchViewActionListener(this, view.getName()));
        this.menuPanel.addComponent(switchView);
    }

    
    
    public void switchView(String viewName) {
        View nextView = this.views.get(viewName);
        View current = this.currentView;
        
        if (nextView.equals(current)) {
            return;
        }
        if (current != null) {
            current.onSwitchAway(nextView);
        }
        
        CardLayout layout = (CardLayout) this.viewContainer.getLayout();
        layout.show(this.viewContainer, viewName);
        
        this.currentView = nextView;
        this.setTitle(WINDOW_CAPTION + " - " + nextView.getName());
        this.viewTitle.setIcon(nextView.getIcon());
        this.viewTitle.setText(nextView.getName());
        
        nextView.onSwitchTo(current);
    }
}
