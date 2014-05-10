package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Equatable;


public interface OrionChatEntry extends Equatable {

    public Date getDate();
    
    public String getSender();
    
    public String getMessage();
    
}