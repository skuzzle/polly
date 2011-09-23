package de.skuzzle.polly.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.skuzzle.polly.installer.script.InstallAction;
import de.skuzzle.polly.installer.script.ScriptAction;
import de.skuzzle.polly.installer.script.ScriptException;
import de.skuzzle.polly.installer.script.UnzipAction;


public class Installer {
    
    public final static String ENV_CONFIG = "%config%";
    public final static String ENV_POLLY = "%polly%";
    public final static String ENV_PLUGINS = "%plugins%";
    public final static String ENV_THIS = "%this%";
    
    private static EnvironmentConstants environment;
    
    public static EnvironmentConstants getEnvironment() {
        return environment;
    }
    
    
    
    public static void main(String[] args) {
        // args length must be 4:
        // [0] = polly root folder (contains polly.jar)
        // [1] = polly config folder
        // [2] = polly plugin folder
        // [3] = semicolon separated list of zip file names
        
        if (args.length != 4) {
            System.out.println("Argument error!");
            return;
        }
        environment = new EnvironmentConstants();
        environment.put(ENV_POLLY, args[0]);
        environment.put(ENV_CONFIG, args[1]);
        environment.put(ENV_PLUGINS, args[2]);
        
        List<String> fileNames = Arrays.asList((args[3].split(";")));
        installAll(fileNames);
        
        try {
            Runtime.getRuntime().exec("java -jar polly.jar -nu");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    
    private static void installAll(List<String> fileNames) {
        TreeStream out = null;
        try {
            FileOutputStream fout = new FileOutputStream("install.log");
            out = new TreeStream(fout, 4);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            out = new TreeStream(System.out, 4);
        }
        
        
        for (String scriptName : fileNames) {
            out.println("INSTALLING: " + scriptName);
            out.indent();
            // Create bootstrap actions for unzipping and installing
            ScriptAction unzip = null;
            File tempDir = null;
            try {
                tempDir = createTempDirectory();
                unzip = new UnzipAction();
                unzip.fromLine(new String[] {"UNZIP", scriptName, tempDir.getAbsolutePath()}, 0);
                unzip.execute(out);
                environment.put(ENV_THIS, tempDir.getAbsolutePath());
            } catch (ScriptException e) {
                out.println(e.getMessage());
                e.printStackTrace();
                unzip.undo(out);
            } catch (IOException e) {
                out.println(e.getMessage());
                continue;
            }           
            

            ScriptAction install = null;
            try {
                File script = new File(tempDir, "install.script");
                if (!script.exists()) {
                    throw new ScriptException("Install script does not exist.");
                }
                install = new InstallAction();
                install.fromLine(new String[] {"INSTALL", script.getAbsolutePath()}, 0);
                install.execute(out);
            } catch (ScriptException e) {
                out.println(e.getMessage());
                e.printStackTrace();
                if (install != null) {
                    install.undo(out);
                    unzip.undo(out);
                }
            }
            
            out.println("FINISH...");
            out.indent();
            install.finish(out);
            unzip.finish(out);
            out.unindent();
            out.unindent();
        }
        out.println("Done");
        out.close();
    }
    
    
    
    public static File createTempDirectory() throws IOException {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + 
                temp.getAbsolutePath());
        }

        if(!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + 
                temp.getAbsolutePath());
        }
        return temp;
    }

}
