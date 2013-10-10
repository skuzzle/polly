package de.skuzzle.polly.core.commandline;

import de.skuzzle.polly.sdk.Configuration;


public class BooleanConfigArgument extends ConfigArgument {

    public BooleanConfigArgument(String name, Configuration config,
        String field) {
        super(name, config, field);
    }
    
    
    
    @Override
    public void execute(String... parameter) throws ParameterException {
        String s = parameter[0];
        final boolean result = Boolean.parseBoolean(s);
        this.config.setProperty(this.field, result);
    }

}
