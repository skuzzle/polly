package de.skuzzle.polly.sdk.httpv2.html;

import java.util.ArrayList;
import java.util.List;


public class HTMLInputGroup extends HTMLInput {

    private final List<HTMLInput> group;
    
    
    public HTMLInputGroup() {
        this.group = new ArrayList<>();
    }
    
    
    public HTMLInputGroup add(HTMLInput input) {
        this.group.add(input);
        return this;
    }
    
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        for (final HTMLInput inp : this.group) {
            b.append(inp.toString());
            b.append(" ");
        }
        return b.toString();
    }
}