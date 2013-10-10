package de.skuzzle.polly.core.commandline;


public class ReturnInfoArgument extends LogInfoArgument {

    public ReturnInfoArgument() {
        super("-returninfo"); //$NON-NLS-1$
    }
    
    
    @Override
    public void execute(String... parameter) throws ParameterException {
        parameter[0] = "Info: " + parameter[0]; //$NON-NLS-1$
        super.execute(parameter);
    }
    
    
    @Override
    public boolean filter() {
        // exclude from canonical arguments 
        return true;
    }
}
