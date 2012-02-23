package polly.porat.gui.util;

import javax.swing.SwingUtilities;

public class SwingInvoke {

    public static void later(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    
    
    public static void andWait(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }
    
    
    
    public static void checkThread() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Invalid cross-thread GUI operation!");
        }
    }

}
