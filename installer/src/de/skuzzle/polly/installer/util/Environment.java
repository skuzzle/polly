package de.skuzzle.polly.installer.util;

import java.io.File;
import java.io.IOException;


public class Environment {

    public final static File POLLY_HOME = new File(".");
    public final static File POLLY_CONFIG_DIR = new File("cfg/");
    public final static File POLLY_CONFIG_FILE = new File("cfg/polly.cfg");
    public final static File POLLY_PLUGIN_DIR = new File("cfg/plugins");
    
    
    
    public static void validate() throws IOException {
        if (!Environment.POLLY_HOME.isDirectory()) {
            throw new IOException("invalid home path: " + Environment.POLLY_HOME);
        } else if (!Environment.POLLY_CONFIG_DIR.isDirectory() || 
                   !Environment.POLLY_CONFIG_FILE.isFile()) {
            throw new IOException("invalid config path");
        } else if (!Environment.POLLY_PLUGIN_DIR.isDirectory()) {
            throw new IOException("invalid plugin path: " + Environment.POLLY_PLUGIN_DIR);
        }
    }
}
