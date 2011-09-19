package polly.commandline;

import polly.PollyConfiguration;


public class PollyArgumentParser extends AbstractArgumentParser {

    private PollyConfiguration config;
    
    
    public PollyArgumentParser(PollyConfiguration config) {
        this.config = config;
        this.createArguments();
    }
    
    
    
    private void createArguments() {
        this.addArgument(new BooleanConfigArgument("-noupdate", this.config, 
        		PollyConfiguration.AUTO_UPDATE));
        this.addArgument(new BooleanConfigArgument("-nu", this.config, 
            PollyConfiguration.AUTO_UPDATE));
        
        this.addArgument(new ConfigArgument("-server", this.config, 
            PollyConfiguration.SERVER));
        this.addArgument(new ConfigArgument("-s", this.config, 
            PollyConfiguration.SERVER));
        
        this.addArgument(new ConfigArgument("-nick", this.config, 
            PollyConfiguration.NICKNAME));
        this.addArgument(new ConfigArgument("-n", this.config, 
            PollyConfiguration.NICKNAME));

        this.addArgument(new ConfigArgument("-ident", this.config, 
            PollyConfiguration.IDENT));
        this.addArgument(new ConfigArgument("-i", this.config, 
            PollyConfiguration.IDENT));

        this.addArgument(new IntegerConfigArgument("-port", this.config, 
            PollyConfiguration.PORT));
        this.addArgument(new IntegerConfigArgument("-p", this.config, 
            PollyConfiguration.PORT));

        this.addArgument(new BooleanConfigArgument("-irclog", this.config, 
            PollyConfiguration.IRC_LOGGING));
        this.addArgument(new BooleanConfigArgument("-il", this.config, 
            PollyConfiguration.IRC_LOGGING));

        this.addArgument(new ConfigArgument("-join", this.config, 
            PollyConfiguration.CHANNELS));
        this.addArgument(new ConfigArgument("-j", this.config, 
            PollyConfiguration.CHANNELS));
        
        this.addArgument(new BooleanConfigArgument("-telnet", this.config, 
            PollyConfiguration.ENABLE_TELNET));
        this.addArgument(new IntegerConfigArgument("-telnetport", this.config, 
            PollyConfiguration.TELNET_PORT));

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
    }
}
