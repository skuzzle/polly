package core.pasteservice;

import java.util.Date;



public interface PasteService {

    public abstract String paste(String message) throws Exception;
    
    public abstract String getName();
    
    public abstract Date getLastPasteTime();
}