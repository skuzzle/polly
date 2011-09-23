package de.skuzzle.polly.installer.script;

import java.io.File;

import de.skuzzle.polly.installer.TreeStream;


public abstract class ScriptAction {
    
    public abstract void execute(TreeStream out) throws ScriptException;

    
    public abstract void fromLine(String[] parts, int line) throws ScriptException;
    
    public void undo(TreeStream out) {}
    
    public void finish(TreeStream out) {}
    
    public boolean readBooleanPart(String part, int line) throws ScriptException {
        if (part.equals("false")) {
            return false;
        } else if (part.equals("true")) {
            return true;
        } else {
            throw new ScriptException("Boolean value 'true' or 'false' expected on line " 
                + line + ", found: " + part);
        }
    }
    
    
    public void parameterError(String commandName, int line) throws ScriptException {
        throw new ScriptException("Invalid parameters for command " + commandName + 
            " on line: " + line);
    }
    
    
    
    public void deleteFolder(File file) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            for (File f : list) {
                deleteFolder(f);
            }
        }
        file.delete();
    }
}