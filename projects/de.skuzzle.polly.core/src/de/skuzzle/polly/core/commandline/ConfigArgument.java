package de.skuzzle.polly.core.commandline;

import de.skuzzle.polly.core.commandline.Argument.ArgumentAction;
import de.skuzzle.polly.sdk.Configuration;


public class ConfigArgument extends Argument implements ArgumentAction {

    protected Configuration config;
    protected String field;
    
    public ConfigArgument(String name, Configuration config, String field) {
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
