package polly.porat.events;

import java.io.File;
import java.util.List;

import polly.porat.logic.GuiController;


public class FilesReceivedEvent {
    
    private GuiController source;
    private List<File> files;
    
    
    
    public FilesReceivedEvent(GuiController source, List<File> files) {
        this.source = source;
        this.files = files;
    }
    
    
    
    public GuiController getSource() {
        return this.source;
    }
    
    
    
    public List<File> getFiles() {
        return this.files;
    }

}
