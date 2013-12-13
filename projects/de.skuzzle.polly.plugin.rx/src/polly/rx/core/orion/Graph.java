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

public class Graph<V, E> {
    
    public final class Node {

        private final V data;
        private final Collection<Edge> edges;
        private final Collection<Node> adjacent;
        
        public Node(V data) {
            if (data == null) {
                throw new NullPointerException("data"); //$NON-NLS-1$
            }
            this.data = data;
            this.edges = new ArrayList<>();
            this.adjacent = new ArrayList<>();
        }
        
        public Edge edgeTo(Node target, E data) {
            final Edge newEdge = new Edge(this, target, data);
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
        
        public V getData() {
            return this.data;
        }
    }
    
    
    
    public final class Edge {
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
        return n;
    }
    
    

    public class Path {
        private final List<Edge> path;
        private final double costs;
        
        
        public Path(List<Edge> path, double costs) {
            this.path = path;
            this.costs = costs;
        }
        
        public List<Edge> getPath() {
            return this.path;
        }
        
        public double getCosts() {
            return this.costs;
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
            return Double.compare(this.costs + heuristics, o.costs + o.heuristics);
        }
    }
    
    
    
    private KnownNode shortestPathInternal(Node start, Node target, 
            LazyBuilder<V, E> builder, Heuristic<V> heuristic, EdgeCosts<E> costs) {
        final PriorityQueue<KnownNode> q = new PriorityQueue<>(this.nodeMap.size());
        final Set<Node> closed = new HashSet<>(this.nodeMap.size());
        
        q.add(new KnownNode(start, 0.0, null, null, 0.0));
        while (!q.isEmpty()) {
            final KnownNode v = q.poll();
            
            if (v.wrapped == target) {
                return v;
            }
            
            if (closed.add(v.wrapped)) {
                builder.collectIncident(this, v.wrapped.getData());
                
                for (final Edge edge : v.wrapped.getIncident()) {
                    final Node v1 = edge.getTarget();
                    
                    if (!closed.contains(v1)) {
                        final double h = heuristic.calculate(
                                v.wrapped.getData(), v1.getData());
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
