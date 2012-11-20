package polly.rx.graphs;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import polly.rx.graphs.Point.PointType;


public class PointSet implements Set<Point>{

    private Color color;
    private final Set<Point> backend;
    private String name;
    
    
    public PointSet() {
        this(Color.BLACK);
    }
    

    
    public PointSet(Color color) {
        this.backend = new TreeSet<Point>();
        this.color = color;
        this.name = "";
    }
    
    
    
    public String getName() {
        return this.name;
    }


    
    public void setName(String name) {
        this.name = name;
    }



    public Color getColor() {
        return this.color;
    }
    
    
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    
    
    @Override
    public boolean add(Point e) {
        return this.backend.add(e);
    }
    
    
    
    public boolean add(double x, double y, PointType type) {
        return this.add(new Point(x, y, type));
    }
    
    
    
    @Override
    public boolean addAll(Collection<? extends Point> c) {
        return this.backend.addAll(c);
    }
    
    
    
    @Override
    public void clear() {
        this.backend.clear();
    }
    
    
    
    @Override
    public boolean contains(Object o) {
        return this.backend.contains(o);
    }
    
    
    
    @Override
    public boolean containsAll(Collection<?> c) {
        return this.backend.containsAll(c);
    }
    
    
    
    @Override
    public boolean isEmpty() {
        return backend.isEmpty();
    }
    
    
    
    @Override
    public Iterator<Point> iterator() {
        return this.backend.iterator();
    }
    
    
    
    @Override
    public boolean remove(Object o) {
        return this.backend.remove(o);
    }
    
    
    
    @Override
    public boolean removeAll(Collection<?> c) {
        return this.backend.removeAll(c);
    }
    
    
    
    @Override
    public boolean retainAll(Collection<?> c) {
        return this.backend.retainAll(c);
    }
    
    
    
    @Override
    public int size() {
        return this.backend.size();
    }
    
    
    
    @Override
    public Object[] toArray() {
        return this.backend.toArray();
    }
    
    
    
    @Override
    public <T> T[] toArray(T[] a) {
        return this.backend.toArray(a);
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (!(obj instanceof PointSet)) {
            return false;
        }
        
        final PointSet other = (PointSet) obj;
        return other.color.equals(this.color) && other.backend.equals(this.backend);
    }
}
