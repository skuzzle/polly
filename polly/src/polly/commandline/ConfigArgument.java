package polly.commandline;

import polly.PollyConfiguration;
import polly.commandline.Argument.ArgumentAction;


public class ConfigArgument extends Argument implements ArgumentAction{

    protected PollyConfiguration config;
    protected String field;
    
    public ConfigArgument(String name, PollyConfiguration config, String field) {
        super(name, 1, null);
        this.config = config;
        this.field = field;
    }
    
    
    
    @Override
    public ArgumentAction getAction() {
        return this;
    }
    


    @Override
    public void execute(String... parameter) throws ParameterException {
        this.config.setProperty(this.field, parameter[0]);
    }
}
