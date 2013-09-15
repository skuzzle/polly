package de.skuzzle.polly.sdk.httpv2.html;

import java.util.ArrayList;
import java.util.List;


public class HTMLElementGroup extends HTMLElement {

    private final List<HTMLElement> group;
    
    
    public HTMLElementGroup() {
        super("");
        this.group = new ArrayList<>();
    }
    
    
    public HTMLElementGroup add(HTMLElement input) {
        this.group.add(input);
        return this;
    }
    
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        for (final HTMLElement inp : this.group) {
            b.append(inp.toString());
            b.append(" ");
        }
        return b.toString();
    }
}