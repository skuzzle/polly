package de.skuzzle.polly.config;


class Comment {

    private boolean isBlockComment;
    private String comment;
    
    
    
    public Comment(String comment, boolean isBlock) {
        this.comment = comment;
        this.isBlockComment = isBlock;
    }
    
    
    
    
    public boolean isBlockComment() {
        return this.isBlockComment;
    }
    
    
    
    public String getComment() {
        return this.comment;
    }
    
    
    
    @Override
    public String toString() {
        if (this.isBlockComment) {
            return "/*" + this.comment + "*/";
        } else {
            return "//" + this.comment;
        }
    }
}