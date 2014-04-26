package polly.rx.httpv2;

import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;


public class ShipId extends Number implements Comparable<ShipId> {

    private static final long serialVersionUID = 1L;
    
    final int id;
    final boolean isAvailabe;
    
    
    public ShipId(int id) {
        this(id, true);
    }
    
    
    public ShipId(int id, boolean isAvailable) {
        this.id = id;
        this.isAvailabe = isAvailable;
    }
    
    
    
    @Override
    public String toString() {
        if (!this.isAvailabe) {
            return "" + this.id; //$NON-NLS-1$
        }
        final String href= RXController.PAGE_SCAN_SHIP_DETAILS + 
                "?shipId=" + id; //$NON-NLS-1$
        return new HTMLElement("a").attr("href", href).content("" + id).toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$;
    }
    
    
    
    @Override
    public int compareTo(ShipId o) {
        return Integer.compare(this.id, o.id);
    }



    @Override
    public int intValue() {
        return this.id;
    }



    @Override
    public long longValue() {
        return this.id;
    }



    @Override
    public float floatValue() {
        return this.id;
    }



    @Override
    public double doubleValue() {
        return this.id;
    }
}