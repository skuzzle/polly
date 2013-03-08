package de.skuzzle.polly.core.parser.util;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.tools.iterators.ArrayIterator;
import de.skuzzle.polly.tools.strings.StringUtils;


public class DotBuilder {

    protected final PrintStream out;
    protected int nodeIdx;
    protected final Map<Node, Integer> nodes;
    
    
    public DotBuilder(PrintStream out) {
        this.out = out;
        this.nodes = new HashMap<Node, Integer>();
        this.out.println("graph \"name\"");
        this.out.println("{");
        this.out.println("node [shape=Mrecord];");
        this.out.println("graph[bgcolor=white, ordering=out, concentrate=true];");
    }
    
    
    
    public void finish() {
        this.out.println("}");
    }
    
    
    
    public void printNode(Node node, String...attributes) {
        Integer i = this.nodes.get(node);
        if (i != null) {
            return;
        }
        i = this.nodeIdx++;
        this.nodes.put(node, i);
        this.out.print("n");
        this.out.print(i);
        this.out.print("[shape=Mrecord, fontname=\"Consolas\", label=\"{");
        
        final Iterator<String> it = ArrayIterator.get(attributes);
        while(it.hasNext()) {
            this.out.print(this.simpleEscape(it.next()));
            if (it.hasNext()) {
                this.out.print("|");
            }
        }
        
        this.out.println("}\"];");
    }
    
    
    
    public void printEdge(Node start, Node target, String label) {
        this.printEdge(start, target, label, "solid", true);
    }
    
    
    
    public void printEdge(Node start, Node target, String label, String style, 
            boolean constraint) {
        final Integer startIdx = this.nodes.get(start);
        final Integer targetIdx = this.nodes.get(target);
        
        if (startIdx == null) {
            throw new IllegalStateException("no start for node " + start);
        } else if (targetIdx == null) {
            throw new IllegalStateException("no target for node " + target);
        }
        
        this.out.print("n");
        this.out.print(startIdx);
        this.out.print("--");
        this.out.print("n");
        this.out.print(targetIdx);
        this.out.print(" [fontname=\"Consolas\", headport=n,tailport=s,constraint=");
        this.out.print(constraint);
        this.out.print(",label=\"");
        this.out.print(label);
        this.out.print("\", style=\"");
        this.out.print(style);
        this.out.println("\"];");
    }
    
    
    private final static char[] ESCAPES = {'?', '<', '>'};
    private final static String[] REPLACEMENTS = {"\\?", "&lt;", "&gt;"};
    
    private String simpleEscape(String s) {
        return StringUtils.replace(s, ESCAPES, REPLACEMENTS);
    }
}
