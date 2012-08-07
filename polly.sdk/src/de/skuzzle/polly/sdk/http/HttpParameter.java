package de.skuzzle.polly.sdk.http;

public class HttpParameter {

    public static enum ParameterType {
        GET, POST;
    }
    
    
    private String key;
    private String value;
    private ParameterType type;
    
    
    
    public HttpParameter(String key, String value, ParameterType type) {
        super();
        this.key = key;
        this.value = value;
        this.type = type;
    }



    public String getKey() {
        return this.key;
    }



    public String getValue() {
        return this.value;
    }



    public ParameterType getType() {
        return this.type;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result
                + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result
                + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HttpParameter other = (HttpParameter) obj;
        if (this.key == null) {
            if (other.key != null)
                return false;
        } else if (!this.key.equals(other.key))
            return false;
        if (this.type != other.type)
            return false;
        if (this.value == null) {
            if (other.value != null)
                return false;
        } else if (!this.value.equals(other.value))
            return false;
        return true;
    }
    
    
    
    @Override
    public String toString() {
        return this.type + "[" + this.key + " = " + this.value + "]";
    }
}