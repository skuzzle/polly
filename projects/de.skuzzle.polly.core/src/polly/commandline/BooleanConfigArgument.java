package polly.commandline;

import de.skuzzle.polly.sdk.Configuration;


public class BooleanConfigArgument extends ConfigArgument {

    public BooleanConfigArgument(String name, Configuration config,
        String field) {
        super(name, config, field);
    }
    
    
    
    @Override
    public void execute(String... parameter) throws ParameterException {
        String s = parameter[0];
        boolean result = false;
        if (s.equals("on") || s.equals("true") || s.equals("yes")) {
            result = true;
        } else if (s.equals("off") || s.equals("false") || s.equals("no")) {
            result = false;
        } else {
            throw new ParameterException("invalid boolean: " + s);
        }
        
        this.config.setProperty(this.field, result);
    }

}
