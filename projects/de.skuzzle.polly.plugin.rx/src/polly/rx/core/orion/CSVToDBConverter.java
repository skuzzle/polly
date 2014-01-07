package polly.rx.core.orion;

import polly.rx.core.orion.model.Quadrant;


public class CSVToDBConverter implements Runnable {

    private final QuadrantProvider quadProvider;
    private final QuadrantUpdater quadUpdater;
    
    
    
    public CSVToDBConverter(QuadrantProvider quadProvider, QuadrantUpdater quadUpdater) {
        this.quadProvider = quadProvider;
        this.quadUpdater = quadUpdater;
    }



    @Override
    public void run() {
        for (final Quadrant quad : this.quadProvider.getAllQuadrants()) {
            try {
                this.quadUpdater.updateSectorInformation(quad.getSectors());
            } catch (OrionException e) {
                e.printStackTrace();
            }
        }
    }

}
