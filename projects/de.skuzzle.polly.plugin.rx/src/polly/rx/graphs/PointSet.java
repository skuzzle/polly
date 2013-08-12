package polly.rx.graphs;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import polly.rx.graphs.Point.PointType;

public class PointSet implements Set<Point> {

    private Color color;
    private Color connectColor;
    private final Set<Point> backend;
    private String name;
    private boolean connect;
    private float strength;



    public PointSet() {
        this(Color.BLACK);
    }



    public PointSet(Color color) {
        this.backend = new TreeSet<Point>();
        this.color = color;
        this.name = "";
        this.strength = 1.f;
    }



    public void setStrength(float strength) {
        this.strength = strength;
    }



    public float getStrength() {
        return this.strength;
    }



    public YScale calculateScale(String name, int steps) {
        final Point first = this.iterator().next();
        double min = first.getY();
        double max = first.getY();
        for (final Point p : this) {
            min = Math.min(min, p.getY());
            max = Math.max(max, p.getY());
        }

        double whole = Math.max(max - min, 1);
        double percentage = Math.ceil(whole * 0.1);

        min = Math.max(0, min - percentage);
        max = max + percentage;
        final int step = (int) Math.ceil((max - min) / steps);
        return new YScale(name, (int) min, (int) max, step);
    }



    public void setConnectColor(Color connectColor) {
        this.connectColor = connectColor;
    }



    public Color getConnectColor() {
        if (this.connectColor == null) {
            return this.color;
        }
        return this.connectColor;
    }



    public void setConnect(boolean connect) {
        this.connect = connect;
    }



    public boolean isConnect() {
        return this.connect;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.backend == null) ? 0 : this.backend.hashCode());
        result = prime * result + ((this.color == null) ? 0 : this.color.hashCode());
        return result;
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
