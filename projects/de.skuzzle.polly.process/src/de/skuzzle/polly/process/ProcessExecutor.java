package de.skuzzle.polly.process;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>This class handles the system dependent task of creating and interacting with 
 * processes. It currently does not support MAC OS.</p>
 * 
 * <p>A concrete instance can be obtained using one of the static factory methods 
 * {@link #getOsInstance(boolean)} or {@link #getOsInstance(boolean, List)}.</p>
 * 
 * <h2>StreamHandlers</h2>
 * When a process is created, it is important to empty its standard out and standard
 * error streams, as otherwise deadlocks may occur (as by the documentation of 
 * {@link Process}). {@link StreamHandler StreamHandlers} are threads that asynchronously
 * read data from the created processes to ensure that all buffers are emptied regularly.
 * A StreamHandler instance can only be attached to a process before it is executed using
 * {@link #setInputHandler(StreamHandler)} and {@link #setErrorHandler(StreamHandler)}. 
 * If no StreamHandler is set, {@link SilentStreamHandler SilentStreamHandlers} are used
 * by default.
 * 
 * <h2>ProcessWatchers</h2>
 * A ProcessWatcher is, like a StreamHandler, a separate thread which is started along
 * with executing a process. It will then be notified when the created process is 
 * shutdown (or optionally a timeout expired).
 * 
 * @author Simon Taddiken
 */
public abstract class ProcessExecutor {
    
    /** Determines whether we are running UNIX */
    public final static boolean IS_UNIX = ProcessExecutor.isUnix();
    
    /** Determines whether we are running on MAC OS */
    public final static boolean IS_MAC = ProcessExecutor.isMac();
    
    /** Determines whether we are running Windows */
    public final static boolean IS_WINDOWS = ProcessExecutor.isWindows();
    
    
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0); 
    }
 
    private static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0); 
    }
 
    private static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("nix") >=0 || os.indexOf("nux") >=0);
    }
    
    
    
    /**
     * Creates a proper {@link ProcessExecutor} for the current OS. MAC OS is currently
     * not supported.
     * 
     * @param runInConsole This flag is system dependent. If set to false, the 
     *          ProcessExecutor will create the new process running in background. If set 
     *          to true on Windows machines, the ProcessExecutor opens a new Console 
     *          window running the process. On Unix machines the executor creates a new 
     *          shell process which then executes the command. 
     * @param commands List of commandline arguments for the created ProcessExecutor.
     * @return A new ProcessExecutor instance.
     */
    public static ProcessExecutor getOsInstance(boolean runInConsole, 
            List<String> commands) {
        
        if (IS_WINDOWS) {
            return new WindowsProcessExecutor(runInConsole, commands);
        } else if (IS_UNIX) {
            return new UnixProcessExecutor(runInConsole, commands);
        } else {
            throw new UnsupportedOperationException("os not supported");
        }
    }
    
    
    
    /**
     * Creates a proper {@link ProcessExecutor} for the current OS. MAC OS is currently
     * not supported.
     * 
     * @param runInConsole This flag is system dependent. If set to false, the 
     *          ProcessExecutor will create the new process running in background. If set 
     *          to true on Windows machines, the ProcessExecutor opens a new Console 
     *          window running the process. On Unix machines the executor creates a new 
     *          shell process which then executes the command. 
     * @return A new ProcessExecutor instance.
     */
    public static ProcessExecutor getOsInstance(boolean runInConsole) {
        return ProcessExecutor.getOsInstance(runInConsole, new LinkedList<String>());
    }
    
    
    
    /**
     * ProcessExecutor implementation for Windows machines.
     * 
     * @author Simon Taddiken
     */
    protected static class WindowsProcessExecutor extends ProcessExecutor {
        
        public WindowsProcessExecutor(boolean runInConsole, List<String> commands) {
            super(runInConsole);
            if (!IS_WINDOWS) {
                throw new IllegalStateException("invalid os");
            }
            if (runInConsole) {
                // HACK: using the 'start' command causes a new window to open but also
                //       creates another process we can not watch. We can only watch the
                //       cmd process. As this process terminates when the other process 
                //       terminates we are at least able to watch for termination.
                
                // TODO: Find a way to redirect the output of the process created
                //       by 'start' to the 'cmd' process. 
                this.commands.add("cmd");
                this.commands.add("/c");
                this.commands.add("start");
                this.commands.add("\"cmd\""); // window title
            }
            this.commands.addAll(commands);
        }
        
        
        
        @Override
        protected Process doStart(ProcessBuilder pb) throws IOException {
            pb.command().addAll(this.commands);
            return pb.start();
        }
        
        
        
        @Override
        protected String escapeCommand(String command) {
            if (command.contains(" ")) {
                command = "\"" + command + "\"";
            }
            return command;
        }
    }
    
    
    
    /**
     * ProcessExecutor implementation for UNIX machines.
     * 
     * @author Simon Taddiken
     */
    protected static class UnixProcessExecutor extends ProcessExecutor {
        private final static List<File> SHELLS = new ArrayList<File>();
        private final static List<String> SHELL_ARGS = new ArrayList<String>();
        
        static {
            SHELLS.add((new File("/bin/bash").getAbsoluteFile()));
            SHELL_ARGS.add("-lic");
            SHELLS.add((new File("/bin/sh").getAbsoluteFile()));
            SHELL_ARGS.add("-c");
        }
        
        private List<String> processCommands;
        
        public UnixProcessExecutor(boolean runInConsole, List<String> commands) {
            super(runInConsole);
            if (!IS_UNIX && !IS_MAC) {
                throw new UnsupportedOperationException("invalid os");
            }
            this.processCommands = new LinkedList<String>();
            if (runInConsole) {
                boolean shellFound = false;
                for (int i = 0; i < SHELLS.size(); ++i) {
                    if (SHELLS.get(i).exists()) {
                        this.processCommands.add(SHELLS.get(i).getAbsolutePath());
                        this.addCommandsFromString(SHELL_ARGS.get(i), this.processCommands);
                        shellFound = true;
                        break;
                    }
                }
                if (!shellFound) {
                    throw new UnsupportedOperationException(
                        "running in console not supported: no shell found");
                }
            }
            this.commands.addAll(commands);
        }
        
        
        
        @Override
        protected Process doStart(ProcessBuilder pb) throws IOException {
            if (this.runInConsole) {
                pb.command().addAll(this.processCommands);
                StringBuilder cmd = new StringBuilder();
                for (String command : this.commands) {
                    cmd.append(command);
                    cmd.append(" ");
                }
                
                pb.command().add("\"" + cmd.toString().trim() + "\"");
            } else {
                pb.command().addAll(this.commands);
            }
            
            return pb.start();
        }
        
        
        
        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.runInConsole) {
                for (String cmd : this.processCommands) {
                    b.append(cmd);
                    b.append(" ");
                }
                b.append("\"");
                b.append(super.toString());
                b.append("\"");
            } else {
                b.append(super.toString());
            }
            return b.toString();
        }
    }    
    
    

    protected List<String> commands;
    private boolean valid;
    protected boolean runInConsole;
    protected File executeIn;
    private StreamHandler input;
    private StreamHandler error;
    private ProcessWatcher watcher;
    private final static Pattern PATTERN = Pattern.compile("[^\\s\"]+|(\"[^\"]+\")");

    
    /**
     * 
     * @param runInConsole This flag is system dependent. If set to false, the 
     *          ProcessExecutor will create the new process running in background. If set 
     *          to true on Windows machines, the ProcessExecutor opens a new Console 
     *          window running the process. On Unix machines the executor creates a new 
     *          shell process which then executes the command. 
     */
    public ProcessExecutor(boolean runInConsole) {
        this.runInConsole = runInConsole;
        this.commands = new LinkedList<String>();
        this.valid = true;
        this.executeIn = new File(".");
        this.input = new SilentStreamHandler("SILENT_INPUT_HANDLER");
        this.error = new SilentStreamHandler("SILENT_ERROR_HANDLER");
    }
    
    
    
    /**
     * Adds a single commandline argument to this executor.
     * 
     * @param cmd The command to add.
     * @return This instance to enable setter chaining.
     */
    public ProcessExecutor addCommand(String cmd) {
        this.commands.add(cmd);
        return this;
    }
    
    
    
    /**
     * Adds a list of commandline arguments to this executor.
     * 
     * @param cmds The commands to add.
     * @return This instance to enable setter chaining.
     */
    public ProcessExecutor addCommand(List<String> cmds) {
        for (String cmd : cmds) {
            this.addCommand(cmd);
        }
        return this;
    }
    

    
    /**
     * Parses the given string like a commandline tool would do. Parts wrapped in 
     * quotes (") will form a single argument, otherwise, the string is splitted at
     * whitespaces.
     * 
     * @param commandLine The string to parse.
     * @return This instance to enable setter chaining.
     */
    public ProcessExecutor addCommandsFromString(String commandLine) {
        this.addCommandsFromString(commandLine, this.commands);
        return this;
    }
    
    
    
    /**
     * Sets the working directory for the process to create.
     *  
     * @param executeIn The working directory.
     * @return This instance to enable setter chaining.
     */
    public ProcessExecutor setExecuteIn(File executeIn) {
        this.executeIn = executeIn;
        return this;
    }
    
    
    
    /**
     * Gets the working directory for the process to create. By default, this will be
     * the directory from which the current JVM is run.
     *  
     * @return The working directory.
     */
    public File getExecuteIn() {
        return this.executeIn;
    }
    
    
    
    
    /**
     * Sets the {@link StreamHandler} that will read all the data from the created 
     * process's standard output.
     * 
     * @param input Standard out StreamHandler.
     * @return This instance to enable setter chaining.
     */
    public ProcessExecutor setInputHandler(StreamHandler input) {
        this.input = input;
        return this;
    }
    
    
    
    /**
     * Gets the {@link StreamHandler} that will read all the data from the created
     * process's standard output.
     * @return The StreamHandler.
     */
    public StreamHandler getInputHandler() {
        return this.input;
    }
    
    
    
    
    /**
     * Gets the {@link StreamHandler} that will read all the data from the created
     * process's standard error output.
     * @return The StreamHandler.
     */
    public StreamHandler getErrorHandler() {
        return this.error;
    }
    
    
    
    /**
     * Sets the {@link StreamHandler} that will read all the data from the created 
     * process's standard error output.
     * 
     * @param error Standard out StreamHandler.
     * @return This instance to enable setter chaining.
     */
    public ProcessExecutor setErrorHandler(StreamHandler error) {
        this.error = error;
        return this;
    }
    
    
    
    /**
     * Sets a {@link ProcessWatcher} which will be notified when the create process is
     * shutdown.
     * 
     * @param watcher The ProcessWatcher to set.
     * @return This instance to enable setter chaining.
     */
    public ProcessExecutor setProcessWatcher(ProcessWatcher watcher) {
        this.watcher = watcher;
        return this;
    }
    
    
    
    protected void addCommandsFromString(String commandLine, List<String> into) {
        Matcher m = PATTERN.matcher(commandLine);
        while (m.find()) {
            String substr = commandLine.substring(m.start(), m.end());
            if (substr.startsWith("\"")) {
                if (!substr.endsWith("\"")) {
                    throw new IllegalArgumentException("commandline format fail: " + 
                        substr);
                }
                substr = substr.substring(1, substr.length() - 1);
            }
            into.add(substr);
        }
    }
    
    
    
    /**
     * <p>Starts the process using the parameters supplied by this class' setter methods.
     * If no custom {@link StreamHandler StreamHandlers} was set, 
     * {@link SilentStreamHandler} will be used by default. By default, no 
     * {@link ProcessWatcher} will be attached if not set by 
     * {@link #setProcessWatcher(ProcessWatcher)}.</p>
     * 
     * <p>Note: this method can only be invoked once on this instance.</p>
     * @return A {@link ProcessWrapper} representing the created process.
     * @throws IOException If creation of the process fails.
     */
    public ProcessWrapper start() throws IOException {
        if (!this.valid) {
            throw new IllegalStateException("already started");
        }
        
        try {
            ProcessBuilder pb = new ProcessBuilder(new LinkedList<String>());
            pb.directory(this.executeIn);
            Process proc = this.doStart(pb);
            ProcessWrapper pw = new ProcessWrapper(proc, this.input, this.error);
            
            this.input.setStream(proc.getInputStream());
            this.error.setStream(proc.getErrorStream());
            
            this.input.start();
            this.error.start();
            
            if (this.watcher != null) {
                this.watcher.setProc(pw);
                this.watcher.start();
            }
            
            return pw;
        } finally {
            this.valid = false;
        }
    }
    
    
    
    /**
     * System dependent method to start a process.
     * 
     * @param pb The {@link ProcessBuilder} that will be used to create the process.
     * @return The created process.
     * @throws IOException If creation of the process fails.
     */
    protected abstract Process doStart(ProcessBuilder pb) throws IOException;
    
    
    
    /**
     * System dependent method to quote a commandline argument when it contains 
     * whitespaces.
     * 
     * @param command The argument to escape.
     * @return The escaped argument.
     */
    protected String escapeCommand(String command) {
        return command;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (String cmd : this.commands) {
            if (cmd.indexOf(" ") != -1) {
                b.append("\"");
                b.append(cmd);
                b.append("\"");
            } else {
                b.append(cmd);
            }
            b.append(" ");
        }
        return b.toString().trim();
    }
}