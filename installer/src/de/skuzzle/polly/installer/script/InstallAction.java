package de.skuzzle.polly.installer.script;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.skuzzle.polly.installer.Installer;
import de.skuzzle.polly.installer.TreeStream;


public class InstallAction extends ScriptAction {

    private String filePath;
    
    private List<ScriptAction> executed;
    
    
    
    @Override
    public void execute(TreeStream out) throws ScriptException {
        File source = new File(this.filePath);
        this.executed = new LinkedList<ScriptAction>();
        
        if (!source.exists()) {
            throw new ScriptException("Source does not exist: " + this.filePath);
        }
        try {
            ScriptParser sp = new ScriptParser(source);
            List<ScriptAction> actions = sp.parse();
            
            for (ScriptAction action : actions) {
                try {
                    action.execute(out);
                    this.executed.add(action);
                } catch (ScriptException e) {
                    out.println(e.getMessage());
                    action.undo(out);
                }
            }
            
        } catch (IOException e) {
            throw new ScriptException("Error while reading source: " + this.filePath);
        }
    }
    
    
    
    @Override
    public void undo(TreeStream out) {
        if (this.executed == null) {
            return;
        }
        for (ScriptAction action : this.executed) {
            action.undo(out);
        }
    }
    
    
    @Override
    public void finish(TreeStream out) {
        if (this.executed == null) {
            return;
        }
        for (ScriptAction action : this.executed) {
            action.finish(out);
        }
    }

    
    
    @Override
    public void fromLine(String[] parts, int line) throws ScriptException {
        if (parts.length == 2) {
            this.filePath = Installer.getEnvironment().resolve(parts[1].trim());
        } else {
            this.parameterError("INSTALL", line);
        }
    }

}
