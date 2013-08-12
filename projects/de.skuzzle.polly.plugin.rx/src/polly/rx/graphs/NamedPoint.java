package polly.rx.graphs;

public class NamedPoint extends Point {

    private final String name;



    public NamedPoint(String name, double x, double y, PointType type) {
        super(x, y, type);
        this.name = name;
    }



    public String getName() {
        return this.name;
    }
    
    
    
    
    public int getIntX() {
        return (int) Math.round(this.getX());
    }
    
    
    
    public int getIntY() {
        return (int) Math.round(this.getY());
    }
}
