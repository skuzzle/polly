package de.skuzzle.polly.process;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public abstract class ProcessExecutor {
    
    public final static boolean IS_UNIX = ProcessExecutor.isUnix();
    public final static boolean IS_MAC = ProcessExecutor.isMac();
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
    
    
    
    public static ProcessExecutor getOsInstance(boolean runInConsole) {
        return ProcessExecutor.getOsInstance(runInConsole, new LinkedList<String>());
    }
    
    
    
    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessExecutor pe = ProcessExecutor.getOsInstance(true);
        System.out.println(System.getProperty("java.home"));
        pe.setExecuteIn(new File("D:\\Dokumente\\Programmieren\\Java Workspace\\polly\\polly\\dist"));

        pe.addCommand(System.getProperty("java.home") + "\\bin\\java.exe");
        pe.addCommandsFromString("-jar polly.jar -u false -n pollyv2 -j #debugging");
        //pe.addCommand("cmd");
        //pe.addCommand("/c");
        //pe.addCommand("dir");
        
        System.out.println(pe.toString());

        /*pe.setProcessWatcher(new ProcessWatcher(30000) {
            @Override
            public void processExit(ProcessWrapper proc, int exitType) {
                System.out.println("PROC EXITED");
                
                if (exitType == ProcessWatcher.EXIT_TYPE_TIMEOUT) {
                    System.out.println("TIMEOUT!");
                    System.out.println(
                        ((BufferedStreamHandler) proc.getInputHandler()).getBuffer().toString());
                } else if (exitType == ProcessWatcher.EXIT_TYPE_ERROR) {
                    System.out.println("ERROR!");
                } else {
                    System.out.println("SUCCESS");
                    System.out.println("CODE = " + proc.getProcess().exitValue());
                }
            }
        });*/
        pe.setInputHandler(new RedirectStreamHandler("INPUT"));
        pe.start();
    }

    
    
    
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

    
    
    public ProcessExecutor(boolean runInConsole) {
        this.runInConsole = runInConsole;
        this.commands = new LinkedList<String>();
        this.valid = true;
        this.executeIn = new File(".");
        this.input = new SilentStreamHandler("SILENT_INPUT_HANDLER");
        this.error = new SilentStreamHandler("SILENT_ERROR_HANDLER");
    }
    
    
    
    public ProcessExecutor addCommand(String cmd) {
        this.commands.add(cmd);
        return this;
    }
    
    
    
    public ProcessExecutor addCommand(List<String> cmds) {
        for (String cmd : cmds) {
            this.addCommand(cmd);
        }
        return this;
    }
    

    
    public ProcessExecutor addCommandsFromString(String commandLine) {
        this.addCommandsFromString(commandLine, this.commands);
        return this;
    }
    
    
    
    public ProcessExecutor setExecuteIn(File executeIn) {
        this.executeIn = executeIn;
        return this;
    }
    
    
    
    public File getExecuteIn() {
        return this.executeIn;
    }
    
    
    
    
    public ProcessExecutor setInputHandler(StreamHandler input) {
        this.input = input;
        return this;
    }
    
    
    
    public StreamHandler getInputHandler() {
        return this.input;
    }
    
    
    
    public StreamHandler getErrorHandler() {
        return this.error;
    }
    
    
    
    public ProcessExecutor setErrorHandler(StreamHandler error) {
        this.error = error;
        return this;
    }
    
    
    
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
    
    
    
    protected abstract Process doStart(ProcessBuilder pb) throws IOException;
    
    
    
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