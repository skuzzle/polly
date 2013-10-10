package de.skuzzle.polly.sdk.httpv2.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HTMLElement {

    private final Map<String, String> attributes;
    private String content;
    private final String tag;



    public HTMLElement(String tag) {
        this.tag = tag;
        this.attributes = new HashMap<>();
    }



    public HTMLElement attr(String name, String val) {
        this.attributes.put(name, val);
        return this;
    }



    public HTMLElement attr(String name) {
        return this.attr(name, null);
    }



    public HTMLElement content(String content) {
        this.content = content;
        return this;
    }
    
    
    
    public HTMLElement content(HTMLElement nested) {
        return this.content(nested.toString());
    }



    public HTMLElement title(String title) {
        return this.attr("title", title); //$NON-NLS-1$
    }



    public HTMLElement type(String type) {
        return this.attr("type", type); //$NON-NLS-1$
    }



    public HTMLElement value(String value) {
        return this.attr("value", value); //$NON-NLS-1$
    }



    public HTMLElement href(String href) {
        return this.attr("href", href); //$NON-NLS-1$
    }



    public HTMLElement src(String src) {
        return this.attr("src", src); //$NON-NLS-1$
    }
    
    
    
    public HTMLElement id(String id) {
        return this.attr("id", id); //$NON-NLS-1$
    }



    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("<"); //$NON-NLS-1$
        b.append(this.tag);
        b.append(" "); //$NON-NLS-1$
        for (final Entry<String, String> e : this.attributes.entrySet()) {
            b.append(e.getKey());
            b.append(" "); //$NON-NLS-1$
            if (e.getValue() != null) {
                b.append("=\""); //$NON-NLS-1$
                b.append(e.getValue());
                b.append("\" "); //$NON-NLS-1$
            }
        }
        if (this.content != null) {
            b.append(">"); //$NON-NLS-1$
            b.append(this.content);
            b.append("</"); //$NON-NLS-1$
            b.append(this.tag);
            b.append(">"); //$NON-NLS-1$
        } else {
            b.append("/>"); //$NON-NLS-1$
        }
        return b.toString();
    }
}
