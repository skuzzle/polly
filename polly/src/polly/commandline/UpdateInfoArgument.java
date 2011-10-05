package polly.commandline;


public class UpdateInfoArgument extends LogInfoArgument {

    public UpdateInfoArgument() {
        super("-updateinfo");
    }
    
    
    @Override
    public void execute(String... parameter) throws ParameterException {
        parameter[0] = "Returned from update: " + parameter[0];
        super.execute(parameter);
    }
    
    
    @Override
    public boolean filter() {
        // exclude from canonical arguments 
        return true;
    }

}
