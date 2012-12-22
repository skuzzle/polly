package de.skuzzle.polly.parsing.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.skuzzle.polly.tools.iterators.ArrayIterator;


public class DotBuilder {
    
    private class Edge {
        Object source;
        Object target;
        
        public Edge(Object source, Object target) {
            super();
            this.source = source;
            this.target = target;
        }
    }

    private int preorderNum;
    private final Stack<Integer> preorderStack;
    private final List<Edge> edgeList;
    private final Map<Object, Integer> preorderMap;
    private final Set<Object> mustPop;
    private final PrintStream out;
    private final boolean directed;
    
    
    public DotBuilder(PrintStream out, boolean directed) {
        this.out = out;
        this.directed = directed;
        this.preorderStack = new LinkedStack<Integer>();
        this.preorderMap = new HashMap<Object, Integer>();
        this.edgeList = new ArrayList<Edge>();
        this.mustPop = new HashSet<Object>();
        
        if (this.directed) {
            this.out.println("digraph \"\"");
        } else {
            this.out.println("graph \"\"");
        }
        this.out.println("{");
        this.out.println("node [shape=box]");
        this.out.println("graph[bgcolor=white, ordering=out]");
    }
    
    
    
    public void finish() {
        for (final Edge edge : this.edgeList) {
            Integer source = this.preorderMap.get(edge.source);
            Integer target = this.preorderMap.get(edge.target);
            
            if (source != null && target != null) {
                this.emitEdge(source, target);
            }
        }
        
        this.out.println("}");
    }
    
    
    
    public void printNodeWithEdgeTo(Object newNode, Object parent, String...attributes) {
        this.printLabel(newNode, false, attributes);
        this.edgeList.add(new Edge(newNode, parent));
    }
    
    
    
    private void printLabel(Object node, boolean edge, String...attributes) {
        int orderNum;
        boolean fromMap = false;
        if (this.preorderMap.get(node) != null) {
            orderNum = this.preorderMap.get(node);
            fromMap = true;
        } else {
            ++this.preorderNum;
            this.preorderMap.put(node, this.preorderNum);
            orderNum = this.preorderNum;
            this.mustPop.add(node);
        }
        
        this.out.print("n");
        this.out.print(orderNum);
        this.out.print("[shape=record, label=\"{");
        
        final Iterator<String> it = ArrayIterator.get(attributes);
        while(it.hasNext()) {
            this.out.print(it.next());
            if (it.hasNext()) {
                this.out.print("|");
            }
        }
        this.out.println("}\"]");
        
        if (edge && !this.preorderStack.isEmpty()) {
            this.emitEdge(this.preorderStack.peek(), orderNum);
        }
        if (!fromMap) {
            this.preorderStack.push(this.preorderNum);
        }
    }
    
    
    
    public void printNode(Object node, String...attributes) {
        this.printLabel(node, true, attributes);
    }
    
    
    
    private void emitEdge(int source, int target) {
        this.out.print("n");
        this.out.print(source);
        if (this.directed) {
            this.out.print(" -> ");
        } else {
            this.out.print(" -- ");
        }
        this.out.print("n");
        this.out.println(target);
    }
    
    
    
    public void pop(Object node) {
        if (this.mustPop.contains(node)) {
            this.preorderStack.pop();
            this.mustPop.remove(node);
        }
    }
}
