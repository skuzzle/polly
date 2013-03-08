package de.skuzzle.polly.core.commandline;

import de.skuzzle.polly.sdk.Configuration;


public class PollyArgumentParser extends AbstractArgumentParser {

    private Configuration config;
    
    
    public PollyArgumentParser(Configuration config) {
        this.config = config;
        this.createArguments();
    }
    
    
    
    private void createArguments() {
        this.addArgument(
            new BooleanConfigArgument("-update", this.config, 
                Configuration.AUTO_UPDATE) {
                    @Override
                    public boolean filter() {
                        return true;
                    }
            });
        this.addArgument(
            new BooleanConfigArgument("-u", this.config, 
                Configuration.AUTO_UPDATE) {
                    @Override
                    public boolean filter() {
                        return true;
                    }
            });
        
        this.addArgument(new ConfigArgument("-server", this.config, 
            Configuration.SERVER));
        this.addArgument(new ConfigArgument("-s", this.config, 
            Configuration.SERVER));
        
        this.addArgument(new ConfigArgument("-nick", this.config, 
            Configuration.NICKNAME));
        this.addArgument(new ConfigArgument("-n", this.config, 
            Configuration.NICKNAME));

        this.addArgument(new ConfigArgument("-ident", this.config, 
            Configuration.IDENT));
        this.addArgument(new ConfigArgument("-i", this.config, 
            Configuration.IDENT));

        this.addArgument(new IntegerConfigArgument("-ports", this.config, 
            Configuration.PORT));
        this.addArgument(new IntegerConfigArgument("-p", this.config, 
            Configuration.PORT));

        this.addArgument(new BooleanConfigArgument("-irclog", this.config, 
            Configuration.IRC_LOGGING));
        this.addArgument(new BooleanConfigArgument("-il", this.config, 
            Configuration.IRC_LOGGING));

        this.addArgument(new ConfigArgument("-join", this.config, 
            Configuration.CHANNELS));
        this.addArgument(new ConfigArgument("-j", this.config, 
            Configuration.CHANNELS));
        
        this.addArgument(new BooleanConfigArgument("-telnet", this.config, 
            Configuration.ENABLE_TELNET));
        this.addArgument(new IntegerConfigArgument("-telnetport", this.config, 
            Configuration.TELNET_PORT));

        this.addArgument(new ConfigArgument("-help", this.config, "") {
            @Override
            public int getParameters() {
                return 0;
            }
            @Override
            public void execute(String... parameter) throws ParameterException {
                throw new ParameterException("showing help");
            } 
        });
        
        this.addArgument(new ConfigArgument("-?", this.config, "") {
            @Override
            public int getParameters() {
                return 0;
            }
            @Override
            public void execute(String... parameter) throws ParameterException {
                throw new ParameterException("showing help");
            } 
        });
        
        this.addArgument(new ReturnInfoArgument());
    }
}
