package de.skuzzle.polly.config;


class ConfigEntry implements Comparable<ConfigEntry> {

    private Comment comment;
    private String name;
    private Object value;
    private ConfigurationFile parentFile;
    private Section parentSection;
    
    
    
    public ConfigEntry(ConfigurationFile parentFile, Section parentSection, 
            Comment comment, String name, Object value) {
        this.parentSection = parentSection;
        this.parentFile = parentFile;
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
        this.parentFile.fireConfigurationChanged(this.parentSection.getName(), this.name);
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



    @Override
    public int compareTo(ConfigEntry other) {
        if (this.name == null || other.name == null) {
            return 0;
        }
        return this.name.compareTo(other.name);
    }
}