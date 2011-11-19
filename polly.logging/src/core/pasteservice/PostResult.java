package core.pasteservice;



public class PostResult {

    private String resultURL;
    
    private String resultString;
    
    

    public PostResult(String resultURL, String resultString) {
        super();
        this.resultURL = resultURL;
        this.resultString = resultString;
    }
    
    
    
    public String getResultString() {
        return resultString;
    }
    
    
    
    public String getResultURL() {
        return resultURL;
    }
}