package de.skuzzle.polly.installer.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import de.skuzzle.polly.installer.Installer;
import de.skuzzle.polly.installer.TreeStream;


public class CopyFolderAction extends ScriptAction {
    
    private String sourceFolder;
    
    private String destinationFolder;
    
    private boolean override;
    
    private List<File> copiedFiles;

    
    @Override
    public void fromLine(String[] parts, int line) throws ScriptException {
        if (parts.length == 3) {
            this.sourceFolder = parts[1];
            this.destinationFolder = parts[2];
            this.override = true;
        } else if (parts.length == 4) {
            this.sourceFolder = parts[1];
            this.destinationFolder = parts[2];
            this.override = this.readBooleanPart(parts[3], line);
        } else {
            this.parameterError("COPYF", line);
        }
        
        this.sourceFolder = Installer.getEnvironment().resolve(this.sourceFolder);
        this.destinationFolder = Installer.getEnvironment().resolve(this.destinationFolder);
    }

    
    
    @Override
    public void execute(TreeStream out) throws ScriptException {
        File source = new File(this.sourceFolder);
        File dest = new File(this.destinationFolder);
        this.copiedFiles = new LinkedList<File>();
        out.println("COPY " + this.sourceFolder + " TO " + this.destinationFolder);
        
        if (!source.isDirectory() || (dest.exists() && !dest.isDirectory())) {
            throw new ScriptException("No directory: " + this.sourceFolder);
        } else if (!this.override && dest.exists()) {
            throw new ScriptException("Destination already exists: " + 
                    this.destinationFolder);
        } else if (dest.exists()) {
            out.indent();
            out.println("Creating backup...");
            out.unindent();
        } else if (!dest.exists() && !dest.mkdirs()) {
            throw new ScriptException("Could not create destination directory: " + 
                    this.destinationFolder);
        }
        
        try {
            out.indent();
            this.copyFolder(source, dest, out);
            
        } catch (IOException e) {
            throw new ScriptException("Error while copying from " + this.sourceFolder + 
                    " to " + this.destinationFolder);
        } finally {
            out.unindent();
        }
    }
    
    
    
    private void copyFolder(File src, File dest, TreeStream out) throws IOException {
        this.copiedFiles.add(dest);
         if (src.isDirectory()) {
             //if directory not exists, create it
            if(!dest.exists()){
               dest.mkdir();
            }
 
            //list all the directory contents
            String files[] = src.list();
 
            for (String file : files) {
               //construct the src and dest file structure
               File srcFile = new File(src, file);
               File destFile = new File(dest, file);
               //recursive copy
               this.copyFolder(srcFile, destFile, out);
            }
 
        } else {
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream o = new FileOutputStream(dest); 
 
            byte[] buffer = new byte[1024];
 
            int length;
            //copy the file content in bytes 
            while ((length = in.read(buffer)) > 0){
               o.write(buffer, 0, length);
            }
            out.println("COPIED " + src.getName() + "...");
            in.close();
            o.close();
        }
    }

    
    
    @Override
    public void undo(TreeStream out) {
        if (this.copiedFiles == null) {
            return;
        }
        for (File file : this.copiedFiles) {
            file.delete();
        }
    }
}
