package de.skuzzle.polly.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import de.skuzzle.polly.installer.util.Environment;
import de.skuzzle.polly.installer.util.FileUtil;
import de.skuzzle.polly.installer.util.PollyConfiguration;



public class Installer {   
    
    
    public static void main(String[] args) {
        // args length must be 1:
        // [0] = semicolon separated list of zip file names
        boolean runPolly = true;
        String pollyParams = "";
        List<String> fileNames = null;
        TreeStream log = null;
        try {
            log = new TreeStream(new FileOutputStream("update.log"), 4);
        } catch (FileNotFoundException e1) {
            log = new TreeStream(System.out, 4);
        }
        
        for (int i = 0; i < args.length; ++i) {
            try {
                if (args[i].equals("-nopolly")) {
                    runPolly = false;
                } else if (args[i].equals("-pp")) {
                    pollyParams = args[++i];
                } else if (args[i].equals("-f")) {
                    fileNames = Arrays.asList((args[++i].split(";")));
                } else {
                    log.println("Unbekannter Parameter: " + args[i]);
                    return;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                log.println("Fehlender Parameter für " + args[i - 1]);
                return;
            }
        }
        
        if (!FileUtil.waitFor("polly.jar")) {
            log.println("polly.jar is not writable. Aborting update.");
            if (fileNames != null) {
                for (String file : fileNames) {
                    new File(file).delete();
                    new File(file).deleteOnExit();
                }
            }
            return;
        }
        
        try {
            Environment.validate();
            if (PollyConfiguration.getInstance() == null) {
                throw new IOException("polly configuration error");
            }
        } catch (IOException e) {
            log.println("environment error: " + e.getMessage());
            return;
        }
        
        // final copy
        final String param = pollyParams;
        if (fileNames != null && !fileNames.isEmpty()) {
            installAll(fileNames, log);
        } else {
            log.println("No files to install");
        }
        
        if (runPolly) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec("java -jar polly.jar -update false " + param);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        System.exit(0);
    }

    
    
    private static void installAll(List<String> fileNames, TreeStream log) {
        File backup = null;
        try {
            log.println("CREATING BACKUP");
            backup = FileUtil.createTempDirectory();
            FileUtil.copyContent(Environment.POLLY_HOME, backup);
        } catch (IOException e) {
            log.println("ERROR WHILE CREATING BACKUP");
            return;
        }
        
        for (String file : fileNames) {
            try {
                installSingle(new File(file), log);
            } catch (IOException e) {
                log.println("ERROR WHILE INSTALLING UPDATE FROM FILE: " + file);
                log.indent();
                log.println(e.getMessage());
                log.unindent();
                log.println("ROLLING BACK ALL CHANGES");
                try {
                    FileUtil.copyContent(backup, Environment.POLLY_HOME);
                    FileUtil.deleteRecursive(backup);
                } catch (IOException e1) {
                    log.println("ERROR WHILE RESTORING BACKUP: " + e1.getMessage());
                    return;
                }
                
            }
        }
        log.println("DELETING BACKUP");
        FileUtil.deleteRecursive(backup);
        PollyConfiguration.getInstance().store();
    }
    
    
    
    private static void installSingle(File file, TreeStream log) throws IOException {
        log.println("INSTALLING " + file);
        try {
            log.indent();
            log.println("CREATING TEMP DIR");
            File temp = FileUtil.createTempDirectory();
            log.println("EXTRACTING " + file + " INTO " + temp);
            FileUtil.unzip(file, temp);
            log.println("COPYING FROM " + temp + " TO " + Environment.POLLY_HOME);
            FileUtil.copyContent(temp, Environment.POLLY_HOME);
            log.println("DELETING TEMP DIR");
            FileUtil.deleteRecursive(temp);
            log.println("DONE");
        } finally {
            log.unindent();
        }
    }
}
