package polly.rx.graphs;


public class Point implements Comparable<Point> {

    private final double x;
    private final double y;
    
    
    public Point(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }
    
    
    
    public double getX() {
        return this.x;
    }
    
    
    
    public double getY() {
        return this.y;
    }


    
    @Override
    public int compareTo(Point o) {
        return this.x == o.x ? Double.compare(this.y, o.y) : Double.compare(this.x, o.x);
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (!(obj instanceof Point)) {
            return false;
        }
        
        final Point other = (Point) obj;
        return other.x == this.x && other.y == this.y;
    }
}