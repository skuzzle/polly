package polly.commandline;

import org.apache.log4j.Logger;

import polly.commandline.Argument.ArgumentAction;


public class LogInfoArgument extends Argument implements ArgumentAction {

    private static Logger logger = Logger.getLogger(LogInfoArgument.class.getName());
    
    public LogInfoArgument(String name) {
        super(name, 1, null);
    }
    
    
    @Override
    public ArgumentAction getAction() {
        return this;
    }
    


    @Override
    public void execute(String... parameter) throws ParameterException {
        logger.info(parameter[0]);
    }
}
