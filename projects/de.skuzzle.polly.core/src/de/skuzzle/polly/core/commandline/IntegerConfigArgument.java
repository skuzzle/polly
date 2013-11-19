package de.skuzzle.polly.core.commandline;

import de.skuzzle.polly.sdk.Configuration;



public class IntegerConfigArgument extends ConfigArgument {

    public IntegerConfigArgument(String name, Configuration config,
        String field) {
        super(name, config, field);
    }
    
    
    
    @Override
    public void execute(String... parameter) throws ParameterException {
        try {
            this.config.setProperty(this.field, Integer.parseInt(parameter[0]));
        } catch (NumberFormatException e) {
            throw new ParameterException(MSG.bind(MSG.invalidInt, parameter[0]));
        }
    }

}
