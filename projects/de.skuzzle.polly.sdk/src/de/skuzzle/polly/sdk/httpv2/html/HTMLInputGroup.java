package de.skuzzle.polly.sdk.httpv2.html;

import java.util.ArrayList;
import java.util.List;


public class HTMLInputGroup extends HTMLElement {

    private final List<HTMLElement> group;
    
    
    public HTMLInputGroup() {
        super("");
        this.group = new ArrayList<>();
    }
    
    
    public HTMLInputGroup add(HTMLElement input) {
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