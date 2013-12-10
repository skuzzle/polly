package polly.rx.core.orion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Graph<V> {
    
    public final class Node {

        private final V data;
        private final Collection<Edge> edges;
        private final Collection<Node> adjacent;
        
        
        
        public Node(V data) {
            this.data = data;
            this.edges = new ArrayList<>();
            this.adjacent = new ArrayList<>();
        }
        
        
        
        public Edge edgeTo(Node target, double costs) {
            final Edge newEdge = new Edge(this, target, costs);
            this.edges.add(newEdge);
            this.adjacent.add(target);
            return newEdge;
        }
        
        
        
        public Collection<Edge> getIncident() {
            return Collections.unmodifiableCollection(this.edges);
        }
        
        

        public Collection<Node> getAdjacent() {
            return Collections.unmodifiableCollection(this.adjacent);
        }
        
        
        public V get() {
            return this.data;
        }
    }
    
    
    
    public final class Edge {
        private final Node source;
        private final Node target;
        private final double costs;
        
        public Edge(Node source, Node target, double costs) {
            super();
            this.source = source;
            this.target = target;
            this.costs = costs;
        }
        

        
        public Node getSource() {
            return this.source;
        }
        
        
        
        public Node getTarget() {
            return this.target;
        }
        
        
        
        public double getCosts() {
            return this.costs;
        }
    }
    
    
    
    public interface LazyBuilder<V> {
        public void collectIncident(Graph<V> source, V data);
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
    
    
    
    public Node getNode(V data) {
        return this.nodeMap.get(data);
    }
    
    
    
    public Node getNode(V data, V putIfAbsend) {
        final Node n = this.nodeMap.get(data);
        if (n == null) {
            return this.addNode(data);
        }
        return null;
    }
    
    

    public static class Path<T> {
        private final List<T> nodes;
        private final double costs;
        
        
        public Path(List<T> nodes, double costs) {
            this.nodes = nodes;
            this.costs = costs;
        }
        
        
        
        
        public List<T> getNodes() {
            return this.nodes;
        }
        
        
        
        public double getCosts() {
            return this.costs;
        }
    }
    
    
    
    public Path<V> findShortestPath(V start, V target, LazyBuilder<V> builder) {
        final Node vStart = this.getNode(start);
        final Node vTarget = this.getNode(target);
        if (vStart  == null || vTarget == null) {
            return new Path<V>(Collections.<V>emptyList(), 0.0);
        }
        final KnownNode node = this.shortestPathInternal(vStart, vTarget, builder);
        if (node == null) {
            return new Path<V>(Collections.<V>emptyList(), 0.0);
        }
        final LinkedList<V> path = new LinkedList<>();
        KnownNode c = node;
        while (c != null) {
            path.addFirst(c.wrapped.get());
            c = c.predecessor;
        }
        return new Path<V>(path, node.costs);
    }
    
    
    
    private class KnownNode implements Comparable<KnownNode> {
        final Node wrapped;
        final KnownNode predecessor;
        final double costs;
        
        
        public KnownNode(Node wrapped, double costs, KnownNode predecessor) {
            this.wrapped = wrapped;
            this.costs = costs;
            this.predecessor = predecessor;
        }
        
        @Override
        public int compareTo(KnownNode o) {
            return Double.compare(costs, o.costs);
        }
    }
    
    
    
    private KnownNode shortestPathInternal(Node start, Node target, LazyBuilder<V> builder) {
        final PriorityQueue<KnownNode> q = new PriorityQueue<>(this.nodeMap.size());
        final Set<KnownNode> closed = new HashSet<>();
        q.add(new KnownNode(start, 0.0, null));
        
        while (!q.isEmpty()) {
            final KnownNode v = q.poll();
            
            if (v.wrapped == target) {
                return v;
            }
            
            if (closed.add(v)) {
                builder.collectIncident(this, v.wrapped.get());
                for (final Edge edge : v.wrapped.getIncident()) {
                    final Node v1 = edge.getTarget();
                    
                    if (!closed.contains(v1)) {
                        q.add(new KnownNode(v1, v.costs + edge.costs, v));
                    }
                }
            }
        }
        
        // no path found
        return null;
    }
}
