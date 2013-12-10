package polly.rx.core.orion.model;

import java.util.Date;

public class Wormhole {

    private String name;
    private Date date;
    private LoadRequired requiresLoad;
    private int minUnload;
    private int maxUnload;
    private Sector source;
    private Sector target;



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



    public Sector getTarget() {
        return this.target;
    }



    public void setTarget(Sector target) {
        this.target = target;
    }

    
    
    public Sector getSource() {
        return this.source;
    }
    
    
    
    public void setSource(Sector source) {
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
