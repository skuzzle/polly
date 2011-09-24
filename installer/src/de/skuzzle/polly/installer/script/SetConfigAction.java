package de.skuzzle.polly.installer.script;

import de.skuzzle.polly.installer.Installer;
import de.skuzzle.polly.installer.TreeStream;


public class SetConfigAction extends ScriptAction {
    
    private String propertyName;
    
    private String propertyValue;
    
    private String backupValue;
    

    @Override
    public void fromLine(String[] parts, int line) throws ScriptException {
        if (parts.length == 3) {
            this.propertyName = parts[1].trim();
            this.propertyValue = parts[2].trim();
        } else {
            this.parameterError("SETPROP", line);
        }
    }
    
    
    
    @Override
    public void execute(TreeStream out) throws ScriptException {
        out.println("SET " + this.propertyName + " = " + this.propertyValue);

        this.backupValue = Installer.getConfig().getProperty(this.propertyName);
        Installer.getConfig().setProperty(this.propertyName, this.propertyValue);
    }
    
    
    
    @Override
    public void undo(TreeStream out) {
        try {
            Installer.getConfig().setProperty(this.propertyName, this.backupValue);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}
