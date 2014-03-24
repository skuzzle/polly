package polly.rx.core.orion.pathplanning;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class Graph<V, E> {
    
    public final class Node implements Equatable {

        private final V data;
        private final Map<Node, Edge> edges;
        private final Collection<Node> edgesFrom;
        
        public Node(V data) {
            if (data == null) {
                throw new NullPointerException("data"); //$NON-NLS-1$
            }
            this.data = data;
            this.edges = new HashMap<>();
            this.edgesFrom = new HashSet<>();
        }
        
        public void removeEdge(Node target) {
            this.edges.remove(target);
            target.edgesFrom.remove(this);
        }
        
        public Edge edgeTo(Node target, E data) {
            if (target == null) {
                throw new NullPointerException("target"); //$NON-NLS-1$
            } else if (data == null) {
                throw new NullPointerException("data"); //$NON-NLS-1$
            } else if (!target.edgesFrom.add(this)) {
                //throw new IllegalStateException("edge already exists"); //$NON-NLS-1$
                // return existing edge
                return this.edges.get(target);
            }
            
            final Edge newEdge = new Edge(this, target, data);
            this.edges.put(target, newEdge);
            return newEdge;
        }
        
        public Collection<Edge> getIncident() {
            return Collections.unmodifiableCollection(this.edges.values());
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
        
        @Override
        public String toString() {
            return this.data.toString();
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
        
        @Override
        public String toString() {
            return "[" + this.source + "] TO [" + this.target + "]";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
        this.removeEdgesTo(data);
        this.nodeMap.remove(data);
    }
    
    
    
    public void removeEdgesTo(V data) {
        final Node n = this.getNode(data);
        if (n == null) {
            return;
        }
        for (final Node v : n.edgesFrom) {
            v.removeEdge(n);
        }
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
    
    
    
    private void searchInternal(V start, Predicate<V> p, LazyBuilder<V, E> builder, 
            Function<Deque<Node>, Node> pollOperation) {
        final Deque<Node> queue = new ArrayDeque<>();
        final Set<Node> visited = new HashSet<>();
        final Node vStart = this.getNode(start, start);
        queue.push(vStart);
        while (!queue.isEmpty()) {
            final Node next = pollOperation.apply(queue);
            if (!p.test(next.getData())) {
                return;
            }
            visited.add(next);
            builder.collectIncident(this, next.getData());
            next.getIncident().forEach(e -> {
                final Node target = e.getTarget();
                if (!visited.contains(target)) {
                    queue.push(e.getTarget());
                }
            });
        }
    }
    
    
    
    public void depthFirstSearch(V start, Predicate<V> p, LazyBuilder<V, E> builder) {
        this.searchInternal(start, p, builder, q -> q.pollLast());
    }
    
    
    
    public void breadthFirstSearch(V start, Predicate<V> p, LazyBuilder<V, E> builder) {
        this.searchInternal(start, p, builder, q -> q.pollFirst());
    }
    
    
    
    public boolean isReachable(V start, V target, LazyBuilder<V, E> builder) {
        final Object[] result = new Object[1];
        final Predicate<V> p = v -> {
            if (v.equals(target)) {
                result[0] = v;
                return false; // abort traversal
            }
            return true;
        };
        depthFirstSearch(start, p, builder);
        return result[0] != null;
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
