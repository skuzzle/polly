package polly.rx.graphs;


public class Point implements Comparable<Point> {

    
    public enum PointType {
        X, DOT, NONE;
    }
    
    private final double x;
    private final double y;
    private final PointType type;
    
    
    
    public Point(double x, double y, PointType type) {
        super();
        this.x = x;
        this.y = y;
        this.type = type;
    }
    
    
    
    public double getX() {
        return this.x;
    }
    
    
    
    public double getY() {
        return this.y;
    }
    
    
    
    public PointType getType() {
        return this.type;
    }


    
    @Override
    public int compareTo(Point o) {
        return this.x == o.x ? Double.compare(this.y, o.y) : Double.compare(this.x, o.x);
    }
    
    
    
    @Override
    public String toString() {
        return "[x=" + this.x + ", y=" + this.y + "]";
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
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