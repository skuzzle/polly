package de.skuzzle.polly.installer.script;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

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
        File cfg = new File(Installer.getEnvironment().get(Installer.ENV_CONFIG));
        Properties p = new Properties();
        
        InputStream input = null;
        try {
            input = new FileInputStream(cfg);
            p.load(input);
            this.backupValue = p.getProperty(this.propertyName);
            p.setProperty(this.propertyName, this.propertyValue);
            
        } catch (IOException e) {
            throw new ScriptException("Error while loading polly configuration.", e);
        } finally {
            this.close(input);
        }

        OutputStream output = null;
        try {
            output = new FileOutputStream(cfg);
            p.store(output, "");
            output.flush();
        } catch (IOException e) {
            throw new ScriptException("Error while storing polly configuration.", e);
        } finally {
            this.close(output);
        }
    }
    
    
    
    @Override
    public void undo(TreeStream out) {
        // TODO Auto-generated method stub
        super.undo(out);
    }
    
    
    
    private void close(Closeable c) throws ScriptException {
        try {
            c.close();
        } catch (Exception e) {
            throw new ScriptException("Error while closing stream.", e);
        }
    }

}
