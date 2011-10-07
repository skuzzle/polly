package polly.util.events;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProcessExecutor {
    
    public final static boolean IS_UNIX = ProcessExecutor.isUnix();
    public final static boolean IS_MAC = ProcessExecutor.isMac();
    public final static boolean IS_WINDOWS = ProcessExecutor.isWindows();
    
    
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf( "win" ) >= 0); 
    }
 
    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf( "mac" ) >= 0); 
    }
 
    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);
    }
    
    
    

    private ProcessBuilder pb;
    private boolean valid;
    private String defaultTerminal;
    private String terminalArguments;
    private final static Pattern PATTERN = Pattern.compile("\\S+|\"[^\"]+\"");

    
    
    public ProcessExecutor() {
        this.pb = new ProcessBuilder(new LinkedList<String>());
        this.defaultTerminal = "xterm";
        this.terminalArguments = "";
        this.valid = true;
    }
    
    
    
    public void addCommand(String cmd) {
        this.pb.command().add(cmd);
    }
    
    
    
    
    public void setDefaultTerminal(String defaultTerminal) {
        this.defaultTerminal = defaultTerminal;
    }
    
    
    
    public void setTerminalArguments(String terminalArguments) {
        this.terminalArguments = terminalArguments;
    }
    
    
    
    public void addCommandsFromString(String commandLine) {
        this.addCommandsFromString(commandLine, this.pb.command());
    }
    
    
    
    private void addCommandsFromString(String commandLine, List<String> into) {
        Matcher m = PATTERN.matcher(commandLine);
        while (m.find()) {
            String substr = commandLine.substring(m.start(), m.end());
            if (substr.startsWith("\"")) {
                if (!substr.endsWith("\"")) {
                    throw new IllegalArgumentException("commandline format fail");
                }
                substr = substr.substring(1, substr.length() - 1);
            }
            into.add(substr);
        }
    }
    
    
    
    
    public Process start() throws IOException {
        return this.start(false);
    }
    
   
    
    
    public synchronized Process start(boolean runInConsole) throws IOException {
        if (!this.valid) {
            throw new IllegalStateException("already started");
        }
        if (runInConsole) {
            LinkedList<String> cmds = new LinkedList<String>();
            if (IS_WINDOWS) {
                cmds.add("cmd");
                cmds.add("/k");
                cmds.add("start");
            } else if (IS_UNIX) {
                String terminal = this.defaultTerminal == null 
                        ? System.getenv("TERM") 
                        : this.defaultTerminal;
                terminal = terminal == null ? "xterm" : terminal;
                cmds.add(terminal);
                this.addCommandsFromString(this.terminalArguments, cmds);
            }
            cmds.addAll(this.pb.command());
            this.pb.command(cmds);
        }
        
        this.valid = false;
        return pb.start();
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (String cmd : this.pb.command()) {
            b.append(cmd);
            b.append(" ");
        }
        return b.toString().trim();
    }
}