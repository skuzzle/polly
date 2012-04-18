package de.skuzzle.polly.config;


class ConfigEntry {

    private Comment myBlockComment;
    private Comment myInlineComment;
    private String name;
    private Object value;
    
    
    
    
    
    public ConfigEntry(Comment myBlockComment, Comment myInlineComment, String name, Object value) {
        this.myBlockComment = myBlockComment;
        this.myInlineComment = myInlineComment;
        this.name = name;
    }
    
    
    
    public void setBlockComment(Comment comment) {
        this.myBlockComment = comment;
    }
    
    
    
    public void setInlineComment(Comment comment) {
        this.myInlineComment = comment;
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
        if (this.myBlockComment != null) {
            b.append(this.myBlockComment.toString());
            b.append(System.lineSeparator());
        }
        b.append(this.name);
        b.append(" = ");
        b.append(this.value.toString());
        if (this.myInlineComment != null) {
            b.append(this.myInlineComment.toString());
        }
        return b.toString();
    }
}