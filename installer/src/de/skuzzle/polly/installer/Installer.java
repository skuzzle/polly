package de.skuzzle.polly.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            System.setOut(log);
        } catch (FileNotFoundException e1) {
            log = new TreeStream(System.out, 4);
        }
        log.println("INSTALL LOG FROM: " + (new Date()).toString());
        
        log.println("ENVIRONMENT");
        log.indent();
        log.println("POLLY_HOME: " + Environment.POLLY_HOME.getAbsolutePath());
        log.println("POLLY_CONFIG: " + Environment.POLLY_CONFIG_DIR.getAbsolutePath());
        log.println("POLLY_CFG: " + Environment.POLLY_CONFIG_FILE.getAbsolutePath());
        log.println("POLLYPLUGINS: " + Environment.POLLY_PLUGIN_DIR.getAbsolutePath());
        log.unindent();
        
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
            e.printStackTrace(log);
            return;
        }
        
        String updateInfo = "";
        if (fileNames != null && !fileNames.isEmpty()) {
            updateInfo = installAll(fileNames, log);
        } else {
            log.println("NO FILES TO INSTALL");
            return;
        }
        
        if (runPolly) {
            final List<String> cmd = new ArrayList<String>(10);
            cmd.add("java");
            cmd.add("-jar");
            cmd.add("polly.jar");
            cmd.add("-update");
            cmd.add("false");
            if (!updateInfo.equals("")) {
                cmd.add("-updateinfo");
                cmd.add(updateInfo);
            }
            
            if (!pollyParams.equals("")) {
                cmd.addAll(Installer.parsePollyCommands(pollyParams));
            }

            log.println("EXECUTING: " + cmd.toString());
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec(cmd.toArray(new String[cmd.size()]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        System.exit(0);
    }

    
    
    private static String installAll(List<String> fileNames, TreeStream log) {
        File backup = null;
        try {
            log.println("CREATING BACKUP");
            log.indent();
            backup = FileUtil.createTempDirectory();
            log.println("BACKUP DIRECTORY: " + backup.getAbsolutePath());
            FileUtil.copyContent(Environment.POLLY_HOME, backup);
        } catch (IOException e) {
            log.println("ERROR WHILE CREATING BACKUP");
            e.printStackTrace(log);
            log.println("DELETING DOWNLOADS");
            for (String file : fileNames) {
                new File(file).delete();
                new File(file).deleteOnExit();
            }
            return "Error while updating: " + e.getMessage();
        } finally {
            log.unindent();
        }
        
        String updateInfo = "";
        int i = 0;
        for (String file : fileNames) {
            try {
                installSingle(new File(file), log);
                ++i;
            } catch (IOException e) {
                updateInfo = "Error while updating: " + e.getMessage();
                log.println("ERROR WHILE INSTALLING UPDATE FROM FILE: " + file);
                log.indent();
                e.printStackTrace(log);
                log.unindent();
                log.println("ROLLING BACK ALL CHANGES");
                try {
                    FileUtil.copyContent(backup, Environment.POLLY_HOME);
                    FileUtil.deleteRecursive(backup);
                } catch (IOException e1) {
                    updateInfo += "; Error while restoring backup: " + e.getMessage();
                    log.println("ERROR WHILE RESTORING BACKUP");
                    e1.printStackTrace(log);
                    return updateInfo;
                }
                
            }
        }
        log.println("DELETING BACKUP");
        FileUtil.deleteRecursive(backup);
        if (PollyConfiguration.getInstance() != null) {
            PollyConfiguration.getInstance().store();
        }
        
        updateInfo = "" + i + " items updated";
        return updateInfo;
    }
    
    
    
    private static void installSingle(File file, TreeStream log) throws IOException {
        log.println("INSTALLING " + file);
        try {
            log.indent();
            log.println("CREATING TEMP DIR");
            File temp = FileUtil.createTempDirectory();
            log.println("EXTRACTING " + file + " INTO " + temp);
            printFileList(FileUtil.unzip(file, temp), log);
            log.println("COPYING FROM " + temp + " TO " + Environment.POLLY_HOME);
            printFileList(FileUtil.copy(temp, Environment.POLLY_HOME), log);
            log.println("DELETING TEMP DIR");
            FileUtil.deleteRecursive(temp);
            log.println("DELETING ZIP FILE");
            file.delete();
            log.println("DONE");
        } finally {
            log.unindent();
        }
    }
    
    
    
    private static void printFileList(List<File> files, TreeStream log) {
        log.indent();
        for (File file : files) {
            log.println(file.getAbsolutePath());
        }
        log.unindent();
    }
    
    
    
    private static List<String> parsePollyCommands(String cmd) {
        Pattern p = Pattern.compile("\\S+|\"[^\"]+\"");
        Matcher m = p.matcher(cmd);
        List<String> result = new LinkedList<String>();
        while (m.find()) {
            String found = cmd.substring(m.start(), m.end());
            if (found.startsWith("\"")) {
                assert found.endsWith("\"");
                found = found.substring(1, found.length() - 1);
            }
            result.add(found);
        }
        return result;
    }
}
