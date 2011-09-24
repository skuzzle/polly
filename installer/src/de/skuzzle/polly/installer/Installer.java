package de.skuzzle.polly.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
    public synchronized static EnvironmentConstants getEnvironment() {
        if (environment == null) {
            environment = new EnvironmentConstants();
            environment.put(ENV_POLLY, new File("./").getAbsolutePath());
            environment.put(ENV_CONFIG, new File("cfg/").getAbsolutePath());
            environment.put(ENV_PLUGINS, new File("cfg/plugins/").getAbsolutePath());
        }
        return environment;
    }
    
    
    
    private static Properties config;
    public synchronized static Properties getConfig() throws ScriptException {
        if (config == null) {
            InputStream input = null;
            try {
                String path = getEnvironment().resolve(ENV_CONFIG);
                path += File.separator + "polly.cfg";
                input = new FileInputStream(path);
                config = new Properties();
                config.load(input);
                
            } catch (IOException e) {
                throw new ScriptException("Error while loading polly configuration.", e);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return config;
    }
    
    
    
    private static void storeConfig() {
        if (config != null) {
            OutputStream out = null;
            try {
                String path = getEnvironment().resolve(ENV_CONFIG);
                path += File.separator + "polly.cfg";
                out = new FileOutputStream(getEnvironment().resolve(path));
                config.store(out, "");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    
    
    public static void main(String[] args) {
        // args length must be 1:
        // [0] = semicolon separated list of zip file names
        boolean runPolly = true;
        String pollyParams = "";
        List<String> fileNames = null;
        
        for (int i = 0; i < args.length; ++i) {
            try {
                if (args[i].equals("-nopolly")) {
                    runPolly = false;
                } else if (args[i].equals("-pp")) {
                    pollyParams = args[++i];
                } else if (args[i].equals("-f")) {
                    fileNames = Arrays.asList((args[++i].split(";")));
                } else {
                    System.out.println("Unbekannter Parameter: " + args[i]);
                    return;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Fehlender Parameter für " + args[i - 1]);
            }
        }
        
        // final copy
        final String param = pollyParams;
        if (fileNames != null && !fileNames.isEmpty()) {
            installAll(fileNames);
        } else {
            System.out.println("No files to install");
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
                    throw new ScriptException("Install script not found");
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
        storeConfig();
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
