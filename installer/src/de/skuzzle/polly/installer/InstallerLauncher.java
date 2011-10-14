package de.skuzzle.polly.installer;

import com.jdtosoft.jarloader.JarClassLoader;


public class InstallerLauncher {

    public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain("de.skuzzle.polly.installer.Installer", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    } 
}