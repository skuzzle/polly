package de.skuzzle.polly.sdk.http;

/**
 * This represents a http request key-value pair. It can be either of type POST or GET.
 * 
 * @author Simon
 * @since 0.9.1
 */
public class HttpParameter {

    /**
     * Http request method type.
     * 
     * @author Simon
     * @since 0.9.1
     */
    public static enum ParameterType {
        /** A Http GET request.  */
        GET,
        /** A Http POST request.  */
        POST;
    }
    
    
    private String key;
    private String value;
    private ParameterType type;
    
    
    /**
     * Creates a new parameter.
     * 
     * @param key The name of the parameter.
     * @param value Its value.
     * @param type Its type.
     */
    public HttpParameter(String key, String value, ParameterType type) {
        super();
        this.key = key;
        this.value = value;
        this.type = type;
    }



    /**
     * Gets the name of this parameter.
     * 
     * @return The name.
     */
    public String getKey() {
        return this.key;
    }



    /**
     * Gets the value of this parameter.
     * 
     * @return The value.
     */
    public String getValue() {
        return this.value;
    }



    /**
     * Gets the type of this parameter.
     * 
     * @return The type.
     */
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