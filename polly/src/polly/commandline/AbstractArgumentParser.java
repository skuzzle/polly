package polly.commandline;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public abstract class AbstractArgumentParser {
       
    protected Set<Argument> arguments;
    private String canonical = "";
    
    public AbstractArgumentParser() {
        this.arguments = new HashSet<Argument>();
    }
    
    
    
    public void addArgument(Argument a) {
        if (!this.arguments.add(a)) {
            throw new IllegalArgumentException("argument already exists: " + a.getName());
        }
    }
    
    
    
    public final void parse(String[] args) throws ParameterException {
        Set<String> tmp = new HashSet<String>();
        
        int i = 0;
        while (i < args.length) {
            String param = args[i];
            ++i;
            
            boolean found = false;
            for (Argument arg : arguments) {
                if (param.equals(arg.getName())) {
                    
                    
                    if (!arg.filter() && !tmp.contains(arg.getName())) {
                        this.canonical += arg.getName() + " ";
                        for (int j = i; j < arg.getParameters() + i; ++j) {
                            this.canonical += args[j] + " ";
                        }
                    }
                    tmp.add(arg.getName());
                    
                    
                    checkParam(args, i, arg.getParameters());
                    
                    String[] p = Arrays.copyOfRange(args, i, i + arg.getParameters() + 1);
                    arg.getAction().execute(p);
                    i += arg.getParameters();
                    found = true;
                }
            }
            if (!found) {
                throw new ParameterException("unknown parameter: " + param);
            }
        }
        
        this.canonical = this.canonical.trim();
    }
    
    
    
    public String getCanonicalArguments() {
        return this.canonical;
    }
    
    
    
    private static void checkParam(String[] args, int current, int expected) 
            throws ParameterException {
        if (args.length < current + expected) {
            throw new ParameterException("further parameters expected");
        }
    }
    
}
