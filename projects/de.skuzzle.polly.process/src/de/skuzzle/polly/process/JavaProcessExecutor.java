package de.skuzzle.polly.process;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;


/**
 * The {@link JavaProcessExecutor} is a factory class which is not instantiable. It
 * has static methods to create a {@link ProcessExecutor} that can create a new java
 * process.
 * 
 * @author Simon
 * @see ProcessExecutor
 */
public final class JavaProcessExecutor {

    /**
     * Gets a {@link ProcessExecutor} that will run <code>java</code> with the given 
     * commandline commands.
     * 
     * @param runInConsole Whether java should b launched using a new console window.
     *          Note: the behavior of this parameter is highly system dependent. See
     *          {@link ProcessExecutor#ProcessExecutor(boolean)}.
     * @param commands Commands to add to the created executor.
     * @return The created ProcessExecutor.
     */
    public static ProcessExecutor getOsInstance(boolean runInConsole, 
            List<String> commands) {
        File java = null;
        if (ProcessExecutor.IS_WINDOWS) {
            java = getJava("sun.boot.library.path", "javaw");
        }
        
        if (java == null || !java.exists()) {
            java = getJava("java.home", "bin/java");
        }
        if (!java.exists()) {
            throw new UnsupportedOperationException("java home error: " + java);
        }
        ProcessExecutor pe = ProcessExecutor.getOsInstance(runInConsole);
        pe.addCommand(java.getAbsolutePath());
        pe.addCommand(commands);
        return pe;
    }

    
    
    private static File getJava(String propertyName, String child) {
        if (ProcessExecutor.IS_WINDOWS) {
            child += ".exe";
        }
        return new File(System.getProperty(propertyName), child);
    }
    
    
    
    /**
     * Creates a ProcessExecutor which will start a new JVM with the same arguments
     * as the current vm.
     * 
     * @param runInConsole Whether java should b launched using a new console window.
     *          Note: the behavior of this parameter is highly system dependent. See
     *          {@link ProcessExecutor#ProcessExecutor(boolean)}.
     * @return The created {@link ProcessExecutor} instance.
     */
    public static ProcessExecutor getCurrentInstance(boolean runInConsole) {
        ProcessExecutor pe = JavaProcessExecutor.getOsInstance(runInConsole);
        List<String> vmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : vmArgs) {
            if (!arg.contains("-agentlib")) {
                pe.addCommand(arg);
            }
        }
        return pe;
    }
    
    
    
    /**
     * Creates a new {@link ProcessExecutor} which can run a new java process. All 
     * commands added to the resulting ProcessExecutor will be arguments for 
     * <code>java</code> or <code>javaw</code>.
     *  
     * @param runInConsole Whether java should b launched using a new console window.
     *          Note: the behavior of this parameter is highly system dependent. See
     *          {@link ProcessExecutor#ProcessExecutor(boolean)}.
     * @return The created {@link ProcessExecutor} instance.
     */
    public static ProcessExecutor getOsInstance(boolean runInConsole) {
        return JavaProcessExecutor.getOsInstance(runInConsole, new LinkedList<String>());
    }
    
    
    private JavaProcessExecutor() {}
}
