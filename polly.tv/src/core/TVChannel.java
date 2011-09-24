package core;

import org.w3c.dom.Element;


public class TVChannel extends XMLEntity {

    private String name;
    
    private int id;
    
    
    public TVChannel(Element e) {
        this.name = this.cleanChannelName(e.getFirstChild().getNodeValue());
        this.id = Integer.parseInt(e.getAttribute("id"));
    }
    
    
    
    private String cleanChannelName(String uncleaned) {
        uncleaned = uncleaned.replace("(niem.)", "");
        uncleaned = uncleaned.replace("S: ", "");
        uncleaned = uncleaned.trim();
        return uncleaned;
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    public String getRequestString() {
        return "channel/" + this.id + "/programs"; 
    }
    
    
    
    @Override
    public String toString() {
        return this.id + " " + this.name;
    }
}