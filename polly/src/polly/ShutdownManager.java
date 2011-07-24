package polly;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.Disposable;


public class ShutdownManager implements Disposable {

    private static Logger logger = Logger.getLogger(ShutdownManager.class.getName());
    private Set<Disposable> shutdownList;
    
    public ShutdownManager() {
        this.shutdownList = new HashSet<Disposable>();
    }
    
    public void addDisposable(Disposable disp) {
        this.shutdownList.add(disp);
    }


    
    @Override
    public void dispose() {
        for (Disposable disp : this.shutdownList) {
            try {
                disp.dispose();
            } catch (Exception e) {
                logger.error("Error while disposing '" + disp + "'", e);
            }
        }
    }
}
