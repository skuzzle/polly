package polly.rx.core.orion;

import java.util.Date;

import polly.rx.entities.QuadSector;

public class Wormhole {

    private String name;

    private Date date;

    private LoadRequired requiresLoad;
    
    private int minUnload;

    private int maxUnload;

    private QuadSector source;
    
    private QuadSector target;



    public String getName() {
        return this.name;
    }



    public void setName(String name) {
        this.name = name;
    }

    
    


    public Date getDate() {
        return this.date;
    }



    public void setDate(Date date) {
        this.date = date;
    }



    public int getMinUnload() {
        return this.minUnload;
    }



    public void setMinUnload(int minUnload) {
        this.minUnload = minUnload;
    }



    public int getMaxUnload() {
        return this.maxUnload;
    }



    public void setMaxUnload(int maxUnload) {
        this.maxUnload = maxUnload;
    }



    public QuadSector getTarget() {
        return this.target;
    }



    public void setTarget(QuadSector target) {
        this.target = target;
    }

    
    
    public QuadSector getSource() {
        return this.source;
    }
    
    
    
    public void setSource(QuadSector source) {
        this.source = source;
    }
    


    public LoadRequired requiresLoad() {
        return requiresLoad;
    }



    public void setRequiresLoad(LoadRequired requiresLoad) {
        this.requiresLoad = requiresLoad;
    }
    
    
    
    @Override
    public String toString() {
        return this.name + " (" + this.minUnload + " - " + this.maxUnload + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
