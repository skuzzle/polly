package polly.commandline;

import polly.PollyConfiguration;


public class IntegerConfigArgument extends ConfigArgument {

    public IntegerConfigArgument(String name, PollyConfiguration config,
        String field) {
        super(name, config, field);
    }
    
    
    
    @Override
    public void execute(String... parameter) throws ParameterException {
        try {
            this.config.setProperty(this.field, Integer.parseInt(parameter[0]));
        } catch (NumberFormatException e) {
            throw new ParameterException("invalid integer: " + parameter[0]);
        }
    }

}
