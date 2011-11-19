package core.pasteservice;



public interface PasteService {

    public abstract String paste(String message) throws Exception;
    
    public abstract String getName();
}