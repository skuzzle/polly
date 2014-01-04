package polly.rx.core.orion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class Graph<V, E> {
    
    public final class Node implements Equatable {

        private final V data;
        private final Collection<Edge> edges;
        private final Collection<Node> edgesFrom;
        
        public Node(V data) {
            if (data == null) {
                throw new NullPointerException("data"); //$NON-NLS-1$
            }
            this.data = data;
            this.edges = new ArrayList<>();
            this.edgesFrom = new HashSet<>();
        }
        
        public void removeEdge(Node target) {
            final Iterator<Edge> it = this.edges.iterator();
            while (it.hasNext()) {
                final Edge next = it.next();
                if (next.getTarget().equals(target)) {
                    it.remove();
                    target.edgesFrom.remove(this);
                    return;
                }
            }
        }
        
        public Edge edgeTo(Node target, E data) {
            if (target == null) {
                throw new NullPointerException("target"); //$NON-NLS-1$
            } else if (data == null) {
                throw new NullPointerException("data"); //$NON-NLS-1$
            } else if (!target.edgesFrom.add(this)) {
                throw new IllegalStateException("edge already exists"); //$NON-NLS-1$
            }
            
            final Edge newEdge = new Edge(this, target, data);
            this.edges.add(newEdge);
            return newEdge;
        }
        
        public Collection<Edge> getIncident() {
            return Collections.unmodifiableCollection(this.edges);
        }
        
        public V getData() {
            return this.data;
        }
        
        @Override
        public boolean equals(Object obj) {
            return EqualsHelper.testEquality(this, obj);
        }

        @Override
        public Class<?> getEquivalenceClass() {
            return Node.class;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean actualEquals(Equatable o) {
            return this.data.equals(((Graph<V, E>.Node) o).data);
        }
    }
    
    
    
    public final class Edge implements Equatable {
        private final Node source;
        private final Node target;
        private final E data;
        
        public Edge(Node source, Node target, E data) {
            if (source == null) {
                throw new NullPointerException("source"); //$NON-NLS-1$
            } else if (target == null) {
                throw new NullPointerException("target"); //$NON-NLS-1$
            } else if (data == null) {
                throw new NullPointerException("data"); //$NON-NLS-1$
            } else if (source == target) {
                throw new IllegalArgumentException("reflexive edge"); //$NON-NLS-1$
            }
            this.source = source;
            this.target = target;
            this.data = data;
        }
        
        
        public Node getSource() {
            return this.source;
        }
        
        
        public Node getTarget() {
            return this.target;
        }
        
        public E getData() {
            return this.data;
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsHelper.testEquality(this, obj);
        }

        @Override
        public Class<?> getEquivalenceClass() {
            return Edge.class;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean actualEquals(Equatable o) {
            return this.data.equals(((Graph<V, E>.Edge) o).data);
        }
    }
    
    
    
    public interface LazyBuilder<V, E> {
        public void collectIncident(Graph<V, E> source, V currentNode);
    }
    
    
    
    public interface Heuristic<V> {
        public double calculate(V v1, V v2);
    }
    
    
    
    public interface EdgeCosts<E> {
        public double calculate(E data);
    }
    
    
    
    public static <E> EdgeCosts<E> constantCosts(final double costs) {
        return new EdgeCosts<E>() {
            @Override
            public double calculate(E data) {
                return costs;
            }
        };
    }

    
    public static <V> Heuristic<V> noHeuristic() {
        return new Heuristic<V>() {
            @Override
            public double calculate(V v1, V v2) {
                return 0;
            }
        };
    }
    
    
    
    private final Map<V, Node> nodeMap;
    
    public Graph() {
        this.nodeMap = new HashMap<>();
    }
    
    
    
    public Node addNode(V data) {
        if (this.nodeMap.containsKey(data)) {
            throw new IllegalArgumentException("node exists"); //$NON-NLS-1$
        }
        final Node newNode = new Node(data);
        this.nodeMap.put(data, newNode);
        return newNode;
    }
    
    
    
    public void removeNode(V data) {
        final Node n = this.getNode(data);
        if (n == null) {
            return;
        }
        for (final Node v : n.edgesFrom) {
            v.removeEdge(n);
        }
        this.nodeMap.remove(data);
    }
    
    
    
    public Node getNode(V data) {
        return this.nodeMap.get(data);
    }
    
    
    
    public Node getNode(V data, V putIfAbsend) {
        final Node n = this.nodeMap.get(data);
        if (n == null) {
            return this.addNode(data);
        }
        return n;
    }
    

    
    public class Path implements Comparable<Path> {
        protected final List<Edge> path;
        protected final double costs;
        protected int count;
        
        public Path(List<Edge> path, double costs) {
            this.path = path;
            this.costs = costs;
        }
        
        public Node getTarget() {
            if (this.path.isEmpty()) {
                return null;
            }
            return this.path.get(this.path.size() - 1).getTarget();
        }
        
        public List<Edge> getPath() {
            return this.path;
        }
        
        public double getCosts() {
            return this.costs;
        }

        @Override
        public int compareTo(Path o) {
            return Double.compare(this.costs, o.costs);
        }
    }
    
    
    
    public Path findShortestPath(V start, V target, LazyBuilder<V, E> builder, 
            Heuristic<V> heuristic, EdgeCosts<E> costs) {
        final Node vStart = this.getNode(start, start);
        final Node vTarget = this.getNode(target, start);
        
        final KnownNode node = this.shortestPathInternal(vStart, vTarget, 
                builder, heuristic, costs);
        if (node == null) {
            return new Path(Collections.<Edge>emptyList(), 0.0);
        }
        final LinkedList<Edge> path = new LinkedList<>();
        KnownNode c = node;
        while (c.predecessor != null) {
            path.addFirst(c.takenEdge);
            c = c.predecessor;
        }
        return new Path(path, node.costs);
    }
    
    
    
    public SortedSet<Path> findShortestPaths(V start, V target, int k, 
            LazyBuilder<V, E> builder, Heuristic<V> heuristic, EdgeCosts<E> costs) {
        
        final Node vStart = this.getNode(start, start);
        final Node vTarget = this.getNode(target, target);
        
        final SortedSet<KnownPath> paths = this.kShortestPathsInternal(vStart, vTarget, k, 
                builder, heuristic, costs);
        final TreeSet<Path> result = new TreeSet<>();
        for (final KnownPath kpath : paths) {
            result.add(new Path(kpath.edges, kpath.costs));
        }
        return result;
    }
    
    
    
    private class KnownNode implements Comparable<KnownNode> {
        final Node wrapped;
        final double heuristics;
        final KnownNode predecessor;
        final Edge takenEdge;
        final double costs;
        
        public KnownNode(Node wrapped, double costs, Edge takenEdge, 
                KnownNode predecessor, double heuristics) {
            if (wrapped == null) {
                throw new NullPointerException("wrapped"); //$NON-NLS-1$
            }
            this.wrapped = wrapped;
            this.costs = costs;
            this.heuristics = heuristics;
            this.takenEdge = takenEdge;
            this.predecessor = predecessor;
        }
        
        @Override
        public int compareTo(KnownNode o) {
            return Double.compare(this.costs + this.heuristics, o.costs + o.heuristics);
        }
    }
    
    
    
    private int count(Map<Node, Integer> count, Node node) {
        Integer i = count.get(node);
        if (i == null) {
            count.put(node, 0);
            i = 0;
        }
        return i;
    }
    
    
    
    private void inc(Map<Node, Integer> count, Node node) {
        Integer i = count.get(node);
        if (i == null) {
            i = 0;
        }
        i = i + 1;
        count.put(node, i);
    }
    
    
    
    private class KnownPath implements Comparable<KnownPath> {
        private final List<Edge> edges;
        private double costs;
        private Node target;
        private double heuristics;
        
        public KnownPath(Collection<Edge> edges, double costs, double heuristics) {
            this.edges = new ArrayList<>(edges);
            this.costs = costs;
            this.heuristics = heuristics;
        }
        
        public KnownPath(double costs, double heuristics) {
            this.edges = new ArrayList<>();
            this.costs = costs;
            this.heuristics = heuristics;
        }
        
        @Override
        public int compareTo(KnownPath o) {
            return Double.compare(this.costs + this.heuristics, o.costs + o.heuristics);
        }
    }
    
    
    
    
    private SortedSet<KnownPath> kShortestPathsInternal(Node start, Node target, int k, 
            LazyBuilder<V, E> builder, Heuristic<V> h, EdgeCosts<E> w) {
        final SortedSet<KnownPath> paths = new TreeSet<>();
        final PriorityQueue<KnownPath> q = new PriorityQueue<>();
        final Map<Node, Integer> count = new HashMap<>();
        
        final KnownPath p = new KnownPath(0.0, 0.0);
        p.target = start;
        q.add(p);
        
        while (!q.isEmpty() && this.count(count, target) < k) {
            final KnownPath next = q.poll();
            final Node v = next.target;
            this.inc(count, v);
            
            if (v.equals(target)) {
                paths.add(next);
            }
            
            if (this.count(count, v) < k) {
                builder.collectIncident(this, v.getData());
                for (final Edge edge : v.getIncident()) {
                    final double heuristics = h.calculate(v.getData(), target.getData());
                    final KnownPath newPath = new KnownPath(next.edges,
                            next.costs + w.calculate(edge.getData()), 
                            heuristics);
                    newPath.edges.add(edge);
                    newPath.target = edge.getTarget();
                    q.add(newPath);
                }
            }
        }
        return paths;
    }
    
    
    
    private KnownNode shortestPathInternal(Node start, Node target, 
            LazyBuilder<V, E> builder, Heuristic<V> heuristic, EdgeCosts<E> costs) {
        final PriorityQueue<KnownNode> q = new PriorityQueue<>(this.nodeMap.size());
        final Set<Node> closed = new HashSet<>(this.nodeMap.size());
        
        q.add(new KnownNode(start, 0.0, null, null, 0.0));
        while (!q.isEmpty()) {
            final KnownNode v = q.poll();
            
            if (v.wrapped.equals(target)) {
                return v;
            }
            
            if (closed.add(v.wrapped)) {
                builder.collectIncident(this, v.wrapped.getData());
                
                for (final Edge edge : v.wrapped.getIncident()) {
                    final Node v1 = edge.getTarget();
                    
                    if (!closed.contains(v1)) {
                        final double h = heuristic.calculate(
                                v.wrapped.getData(), 
                                target.getData());
                        q.add(new KnownNode(v1, 
                                v.costs + costs.calculate(edge.getData()), edge, v, h));
                    }
                }
            }
        }
        
        // no path found
        return null;
    }
}
