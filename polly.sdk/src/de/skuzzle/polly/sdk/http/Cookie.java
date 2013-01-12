package de.skuzzle.polly.sdk.http;


public class Cookie {

    private final String name;
    private final String value;
    private final String domain;
    private final int maxAge;
    
    
    
    public Cookie(String name, String value, String domain, int maxAge) {
        super();
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.maxAge = maxAge;
    }



    public Cookie(String name, String value, int maxAge) {
        this(name, value, null, maxAge);
    }





    @Override
    public String toString() {
        return this.name + "=" + this.value + ";Version=1;Max-Age=" + 
            Integer.toString(this.maxAge) + 
            (this.domain != null ? ";Domain=" + this.domain : "");
    }
}
