package polly.commandline;


public class Argument {
    
    public static interface ArgumentAction {
        public abstract void execute(String...parameter) throws ParameterException;
    }

    private String name;
    
    private int parameters;
    
    private ArgumentAction action;
    
    
    public Argument(String name, int parameters, ArgumentAction action) {
        this.name = name;
        this.parameters = parameters;
        this.action = action;
    }
    
    
    public String getName() {
        return this.name;
    }
    
    
    public int getParameters() {
        return this.parameters;
    }
    
    
    public ArgumentAction getAction() {
        return this.action;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Argument other = (Argument) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}