package de.skuzzle.polly.installer.script;

import java.io.File;

import de.skuzzle.polly.installer.Installer;
import de.skuzzle.polly.installer.SilentStream;
import de.skuzzle.polly.installer.TreeStream;


public class DeleteFolderAction extends ScriptAction {

    private String folder;
    
    private File backupFolder;
    
    private ScriptAction copy;
    
    
    @Override
    public void execute(TreeStream out) throws ScriptException {
        File dir = new File(this.folder);
        out.println("DELETE " + this.folder);
        
        try {
            this.backupFolder = Installer.createTempDirectory();
            
            out.indent();
            out.println("Creating backup...");
            this.copy = new CopyFolderAction();
            this.copy.fromLine(new String[] {"COPYF", dir.getAbsolutePath(), 
                    this.backupFolder.getAbsolutePath()}, 0);
            this.copy.execute(new TreeStream(new SilentStream(), 4));
        } catch (Exception e) {
            throw new ScriptException("Error while creating backup", e);
        } finally {
            out.unindent();
        }

        this.deleteFolder(dir);
    }
    
    
    
    @Override
    public void finish(TreeStream out) {
        if (this.backupFolder != null) {
            out.println("DELETE backup " + this.backupFolder.getAbsolutePath());
            this.backupFolder.delete();
        }
    }
    
    
    
    @Override
    public void undo(TreeStream out) {
        ScriptAction copy = new CopyFolderAction();
        try {
            out.println("RESTORING deleted files from backup");
            copy.fromLine(new String[] {"COPYF", this.backupFolder.getAbsolutePath(), 
                this.folder}, 0);
            copy.execute(new TreeStream(new SilentStream(), 4));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
    
    

    @Override
    public void fromLine(String[] parts, int line) throws ScriptException {
        if (parts.length == 2) {
            this.folder = Installer.getEnvironment().resolve(parts[1].trim());
        } else {
            this.parameterError("DELETE", line);
        }
        this.folder = this.escapePath(this.folder);
    }

}
