package de.skuzzle.polly.installer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class PollyConfiguration extends Properties {

    private static final long serialVersionUID = 1L;
    private static PollyConfiguration instance;
    
    public static synchronized PollyConfiguration getInstance() {
        if (PollyConfiguration.instance == null) {
            PollyConfiguration.instance = new PollyConfiguration(Environment.POLLY_CONFIG_FILE);
            InputStream in = null;
            try {
                in = new FileInputStream(Environment.POLLY_CONFIG_FILE);
                PollyConfiguration.instance.load(in);
            } catch (IOException e) {
                PollyConfiguration.instance = null;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return PollyConfiguration.instance;
    }
    
    
    
    
    private File store;
    private Set<String> forbiddenProperties;
    
    
    private PollyConfiguration(File store) {
        this.store = store;
        this.forbiddenProperties = new HashSet<String>();
    }
    
    
    
    @Override
    public synchronized Object setProperty(String key, String value) {
        if (this.forbiddenProperties.contains(key)) {
            return null;
        }
        return super.setProperty(key, value);
    }


    
    public void store() {
        OutputStream out = null;
        try {
            out = new FileOutputStream(this.store);
            this.store(out, "");
        } catch (IOException ignore) {
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
