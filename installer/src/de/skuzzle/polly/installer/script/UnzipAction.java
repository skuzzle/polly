package de.skuzzle.polly.installer.script;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import de.skuzzle.polly.installer.Installer;
import de.skuzzle.polly.installer.SilentStream;
import de.skuzzle.polly.installer.TreeStream;


public class UnzipAction extends ScriptAction {
    
    private String zipFile;
    
    private String destinationFolder;
    
    private List<File> extracted;
    
    private File backupFolder;
    
    private ScriptAction copy;

    
    @Override
    public void fromLine(String[] parts, int line) throws ScriptException {
        if (parts.length == 3) {
            this.zipFile = parts[1];
            this.destinationFolder = parts[2];
        } else {
            this.parameterError("UNZIP", line);
        }
        
        this.zipFile = Installer.getEnvironment().resolve(this.zipFile);
        this.destinationFolder = Installer.getEnvironment().resolve(this.destinationFolder);
    }
    
    
    
    @Override
    public void execute(TreeStream out) throws ScriptException {
        File zip = new File(this.zipFile);
        File dest = new File(this.destinationFolder);
        this.extracted = new LinkedList<File>();
        out.println("UNZIP " + this.zipFile + " INTO " + this.destinationFolder);
        
        if (!zip.exists()) {
            throw new ScriptException("Sourcefile does not exist: " + this.zipFile);
        } else if (dest.exists() && !dest.isDirectory()) {
            throw new ScriptException("Destination is no directoy: " + 
                    this.destinationFolder);
        } else if (dest.exists()) {
            // make a backup
            out.indent();
            out.println("Backing up existing files...");
            try {
                this.backupFolder = Installer.createTempDirectory();
                this.copy = new CopyFolderAction();
                this.copy.fromLine(new String[] {"COPYF", dest.getAbsolutePath(), 
                    this.backupFolder.getAbsolutePath()} , 0);      
                this.copy.execute(new TreeStream(new SilentStream(), 4));
            } catch (Exception e) {
                throw new ScriptException("Error while backing up existing files.");
            } finally {
                out.unindent();
            }
            
        } else if (!dest.exists() && !dest.mkdirs()) {
            throw new ScriptException("Could not create destination folder: " + 
                this.destinationFolder);
        }
        
        try {
            out.indent();
            this.unzip(zip, dest, out);
        } catch (IOException e) {
            throw new ScriptException("Error while unzipping " + this.zipFile, e);
        } finally {
            //XXX:
            out.unindent();
        }
    }


    
    private File unzip(File zip, File to, TreeStream out) throws ZipException, IOException {
        ZipFile zipFile = new ZipFile(zip);
        
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            
            out.println("EXTRACTING " + entry.getName() + "...");
            
            BufferedInputStream in =  new BufferedInputStream(
                zipFile.getInputStream(entry));
            
            File next = new File(to, entry.getName());
            this.extracted.add(next);
            if (!next.isDirectory()) {
                next.getParentFile().mkdirs();
            }
            
            if (entry.isDirectory()) {
                next.mkdirs();
                continue;
            }
            BufferedOutputStream o = new BufferedOutputStream(
                new FileOutputStream(next));

            byte[] buffer = new byte[2048];
            int bytes = in.read(buffer);
            while (bytes != -1) {
                o.write(buffer, 0, bytes);
                bytes = in.read(buffer);
            }
            o.flush();
            o.close();
            in.close();
        }
        zipFile.close();
        return to;
    }
    
    
    
    @Override
    public void undo(TreeStream out) {
        if (this.extracted == null) {
            return;
        }
        this.deleteFolder(new File(this.destinationFolder));
        ScriptAction replay = new CopyFolderAction();
        
        try {
            replay.fromLine(new String[] {"COPYF", this.backupFolder.getAbsolutePath(), 
                this.destinationFolder}, 0);
            replay.execute(out);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    
    
    @Override
    public void finish(TreeStream out) {
        if (this.backupFolder != null) {
            out.println("DELETE backup " + this.backupFolder.getAbsolutePath());
            this.deleteFolder(backupFolder);
        }
    }

}
