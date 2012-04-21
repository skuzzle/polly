package de.skuzzle.polly.config;


class ConfigEntry {

    private Comment comment;
    private String name;
    private Object value;
    
    
    
    public ConfigEntry(Comment comment, String name, 
                Object value) {
        this.comment = comment;
        this.name = name;
        this.value = value;
    }
    
    
    
    public void setBlockComment(Comment comment) {
        this.comment = comment;
    }
    
    
    
    public Comment getComment() {
        return this.comment;
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    protected Object getValue() {
        return this.value;
    }

    
    
    protected void setValue(Object value) {
        this.value = value;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (this.comment != null) {
            b.append(this.comment.toString());
            b.append(System.lineSeparator());
        }
        b.append(this.name);
        b.append(" = ");
        b.append(this.value.toString());
        return b.toString();
    }
}