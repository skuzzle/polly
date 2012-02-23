package polly.porat.gui.images;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;


public class Icons {

    
    public final static Icon TOOLBAR_ICON = loadIcon("widgets.png");
    public final static Icon LOG_VIEW_ICON = loadIcon("cctv_camera.png");
    public final static Icon IRC_VIEW_ICON = loadIcon("terminal.png");
    
    public final static Icon PING_0_ICON = loadIcon("ping_0.png");
    public final static Icon PING_1_ICON = loadIcon("ping_1.png");
    public final static Icon PING_2_ICON = loadIcon("ping_2.png");
    public final static Icon PING_3_ICON = loadIcon("ping_3.png");
    public final static Icon PING_4_ICON = loadIcon("ping_4.png");
    public final static Icon PING_5_ICON = loadIcon("ping_5.png");
    
    public final static Icon CROSS_ICON = loadIcon("cross.png");
    public final static Icon NETWORKING_ICON = loadIcon("networking_green.png");
    public final static Icon CONNECTED_ICON = loadIcon("connect.png");
    public final static Icon DISCONNECTED_ICON = loadIcon("disconnect.png");
    public final static Icon DRIVE_ICON = loadIcon("drive.png");
    
    
    public static Icon loadIcon(String path) {
        URL imgUrl = Icons.class.getResource(path);
        return new ImageIcon(imgUrl);
    }
}
