package polly.core;


public class ModuleDependencyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModuleDependencyException() {
        super();
    }

    public ModuleDependencyException(String arg0, Throwable arg1, boolean arg2,
            boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public ModuleDependencyException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ModuleDependencyException(String arg0) {
        super(arg0);
    }

    public ModuleDependencyException(Throwable arg0) {
        super(arg0);
    }

    
}
